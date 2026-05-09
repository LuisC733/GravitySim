package com.nbodysim.renderer;

import java.util.ArrayList;

import com.nbodysim.Config;
import com.nbodysim.physics.Body;

class GridPotentialTree {
    private static final float MIN_HALF_WIDTH = 0.0001f;

    private final float theta;
    private final float epsilon;
    private Node root;

    GridPotentialTree(float theta, float epsilon) {
        this.theta = theta;
        this.epsilon = epsilon;
    }

    void rebuild(ArrayList<Body> bodies) {
        if (bodies.isEmpty()) {
            root = null;
            return;
        }

        float minX = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY;
        float maxZ = Float.NEGATIVE_INFINITY;

        for (Body body : bodies) {
            float x = toGridCoordinate(body.position.x);
            float z = toGridCoordinate(body.position.z);
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minZ = Math.min(minZ, z);
            maxZ = Math.max(maxZ, z);
        }

        float centerX = (minX + maxX) * 0.5f;
        float centerZ = (minZ + maxZ) * 0.5f;
        float halfWidth = Math.max(maxX - minX, maxZ - minZ) * 0.55f;
        root = new Node(centerX, centerZ, Math.max(halfWidth, 1.0f));

        for (Body body : bodies) {
            root.insert(toPointMass(body));
        }
    }

    float potentialAt(float x, float z) {
        if (root == null) {
            return 0.0f;
        }
        return root.potentialAt(x, z);
    }

    private PointMass toPointMass(Body body) {
        return new PointMass(
                toGridCoordinate(body.position.x),
                toGridCoordinate(body.position.z),
                (float) Math.log(body.mass));
    }

    private float toGridCoordinate(double position) {
        return (float) (position / Config.SCALE);
    }

    private class Node {
        private final float centerX;
        private final float centerZ;
        private final float halfWidth;
        private final Node[] children = new Node[4];

        private PointMass pointMass;
        private float totalWeight;
        private float weightedCenterX;
        private float weightedCenterZ;

        Node(float centerX, float centerZ, float halfWidth) {
            this.centerX = centerX;
            this.centerZ = centerZ;
            this.halfWidth = halfWidth;
        }

        void insert(PointMass newPointMass) {
            addToAggregate(newPointMass);

            if (isLeaf() && pointMass == null) {
                pointMass = newPointMass;
                return;
            }

            if (halfWidth <= MIN_HALF_WIDTH) {
                pointMass = null;
                return;
            }

            if (isLeaf()) {
                PointMass oldPointMass = pointMass;
                pointMass = null;
                childFor(oldPointMass).insert(oldPointMass);
            }

            childFor(newPointMass).insert(newPointMass);
        }

        float potentialAt(float x, float z) {
            if (totalWeight == 0.0f) {
                return 0.0f;
            }

            if (pointMass != null) {
                return contribution(pointMass.weight, pointMass.x, pointMass.z, x, z);
            }

            if (isLeaf()) {
                return contribution(totalWeight, weightedCenterX, weightedCenterZ, x, z);
            }

            float dx = x - weightedCenterX;
            float dz = z - weightedCenterZ;
            float distance = softenedDistance(dx, dz);
            float width = halfWidth * 2.0f;

            if (width / distance < theta) {
                return totalWeight / distance;
            }

            float sum = 0.0f;
            for (Node child : children) {
                if (child != null) {
                    sum += child.potentialAt(x, z);
                }
            }
            return sum;
        }

        private void addToAggregate(PointMass newPointMass) {
            float previousWeight = totalWeight;
            totalWeight += newPointMass.weight;

            if (previousWeight == 0.0f) {
                weightedCenterX = newPointMass.x;
                weightedCenterZ = newPointMass.z;
                return;
            }

            weightedCenterX = (weightedCenterX * previousWeight + newPointMass.x * newPointMass.weight) / totalWeight;
            weightedCenterZ = (weightedCenterZ * previousWeight + newPointMass.z * newPointMass.weight) / totalWeight;
        }

        private boolean isLeaf() {
            for (Node child : children) {
                if (child != null) {
                    return false;
                }
            }
            return true;
        }

        private Node childFor(PointMass pointMass) {
            int quadrant = quadrantFor(pointMass);
            if (children[quadrant] == null) {
                children[quadrant] = new Node(childCenterX(quadrant), childCenterZ(quadrant), halfWidth * 0.5f);
            }
            return children[quadrant];
        }

        private int quadrantFor(PointMass pointMass) {
            int quadrant = 0;
            if (pointMass.x >= centerX) {
                quadrant |= 2;
            }
            if (pointMass.z >= centerZ) {
                quadrant |= 1;
            }
            return quadrant;
        }

        private float childCenterX(int quadrant) {
            float offset = halfWidth * 0.5f;
            return (quadrant & 2) == 0 ? centerX - offset : centerX + offset;
        }

        private float childCenterZ(int quadrant) {
            float offset = halfWidth * 0.5f;
            return (quadrant & 1) == 0 ? centerZ - offset : centerZ + offset;
        }
    }

    private float contribution(float weight, float sourceX, float sourceZ, float targetX, float targetZ) {
        float dx = targetX - sourceX;
        float dz = targetZ - sourceZ;
        return weight / softenedDistance(dx, dz);
    }

    private float softenedDistance(float dx, float dz) {
        return (float) Math.sqrt(dx * dx + dz * dz + epsilon * epsilon);
    }

    private static class PointMass {
        final float x;
        final float z;
        final float weight;

        PointMass(float x, float z, float weight) {
            this.x = x;
            this.z = z;
            this.weight = weight;
        }
    }
}
