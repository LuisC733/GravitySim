package com.nbodysim.core;

import com.nbodysim.physics.*;

class Octree {
    class Node{
        Vector3D center;
        double halfWidth;
        Vector3D centerOfMass;
        double totalMass;
        Node[] children;
        Body body;

        Node(Vector3D center, double halfWidth){
            this.center= center;
            this.halfWidth = halfWidth;
            this.centerOfMass = null;
            this.totalMass = 0;
            this.children = new Node[8];
            this.body =  null;
        }
        boolean isLeaf(){
            for(int i = 0; i < children.length; i++){
                if(children[i] != null) return false;
            }
            return true;
        }
        Vector3D getChildCenter(int octant){
            double x = 0, y = 0, z = 0;
            if((octant & 4) != 0) x = center.x + halfWidth / 2;
            if((octant & 4) == 0) x = center.x - halfWidth / 2;
            if((octant & 2) != 0) y = center.y + halfWidth / 2;
            if((octant & 2) == 0) y = center.y - halfWidth / 2;
            if((octant & 1) != 0) z = center.z + halfWidth / 2;
            if((octant & 1) == 0) z = center.z - halfWidth / 2;
            return new Vector3D(x, y, z);
        }
        void updateDistributionMass(Body body){
            double prevMass = totalMass;
            totalMass += body.mass;
            if(centerOfMass == null) centerOfMass = body.position;
            else centerOfMass = centerOfMass.scale(prevMass).add(body.position.scale(body.mass)).scale(1.0 / totalMass);
        }
        int getOctant(Vector3D center, Vector3D bodyPos){
            int index = 0;
            if(bodyPos.x >= center.x) index |= 4;
            if(bodyPos.y >= center.y) index |= 2;
            if(bodyPos.z >= center.z) index |= 1;
            return index;
        }
    }
    void insert(Node node, Body newBody){
        if(node.halfWidth < 1e-10){
            node.updateDistributionMass(newBody);
            return;
        }

        if(node.body == null && node.isLeaf()){
            node.body = newBody;
        }
        else if(node.body != null){
            int i = node.getOctant(node.center, node.body.position);
            if(node.children[i] == null){
                node.children[i] = new Node(node.getChildCenter(i), node.halfWidth / 2);
            }
            insert(node.children[i], node.body);
            node.body = null;

            int j = node.getOctant(node.center, newBody.position);
            if(node.children[j] == null){
                node.children[j] = new Node(node.getChildCenter(j), node.halfWidth / 2);
            }
            insert(node.children[j], newBody);
        }
        else{ 
            int i = node.getOctant(node.center, newBody.position);
            if(node.children[i] == null){
                node.children[i] = new Node(node.getChildCenter(i), node.halfWidth / 2);
            }
            insert(node.children[i], newBody);
        }
        node.updateDistributionMass(newBody);
    }
    Vector3D traverse(Node node, Body body){
        Gravity g = new Gravity();
        Vector3D sumOfForce = new Vector3D(0,0,0);
        
        if(node.body == null){
            return new Vector3D(0, 0, 0);
        }
        else if(node.body != null && node.isLeaf()){
            return g.calculateForce(node.body, body);
        }
        else if(node.body == null && !node.isLeaf()){
            double width = node.halfWidth * 2;
            double distance = (node.centerOfMass.sub(body.position)).magnitude();
            double theta = 1.0;

            if(width / distance < theta){
                Body dummy = new Body(node.centerOfMass, new Vector3D(0,0,0), node.totalMass, 0);
                return g.calculateForce(dummy, body);
            }
            else{
                for(int i = 0; i < node.children.length; i++){
                    sumOfForce.add(traverse(node.children[i], body));
                }
            }
        }
        return sumOfForce;
    }
}