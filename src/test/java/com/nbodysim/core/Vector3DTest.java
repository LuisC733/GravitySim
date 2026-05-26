package com.nbodysim.core;

import com.nbodysim.Config;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class Vector3DTest {

    private void assertVectorEquals(Vector3D exp, Vector3D vecActual){
        assertEquals(exp.x, vecActual.x, Config.TOLERANCE);
        assertEquals(exp.y, vecActual.y, Config.TOLERANCE);
        assertEquals(exp.z, vecActual.z, Config.TOLERANCE);
    }

    @Test
    void add_returnsComponentWiseSum(){
        var vec1 = new Vector3D(1, 2, 3);
        var vec2 = new Vector3D(4, 5, 6);
        var exp = new Vector3D(5, 7, 9);
        assertVectorEquals(exp, vec1.add(vec2));
    }

    @Test
    void add_withZeroVectorReturnsSameVector(){
        var vec1 = new Vector3D(1, 2, 3);
        var vec2 = new Vector3D(0, 0, 0);
        var exp = new Vector3D(1, 2, 3);
        assertVectorEquals(exp, vec1.add(vec2));
    }

    @Test
    void add_doesNotModifyOriginalVectors(){
        var vec1 = new Vector3D(1, 2, 3);
        var vec2 = new Vector3D(4, 5, 6);
        var exp1 = new Vector3D(1, 2, 3);
        var exp2 = new Vector3D(4, 5, 6);
        vec1.add(vec2);
        assertVectorEquals(exp1, vec1);
        assertVectorEquals(exp2, vec2);
    }

    @Test
    void sub_returnsComponentWiseDifference(){
        var vec1 = new Vector3D(4, 5, 6);
        var vec2 = new Vector3D(1, 2, 3);
        var exp = new Vector3D(3, 3, 3);
        assertVectorEquals(exp, vec1.sub(vec2));
    }

    @Test
    void sub_vectorMinusItselfReturnsZeroVector(){
        var vec = new Vector3D(1, 2, 3);
        var exp = new Vector3D(0, 0, 0);
        assertVectorEquals(exp, vec.sub(vec));
    }

    @Test
    void scale_multipliesAllComponents(){
        var vec = new Vector3D(1, 2, 3);
        double factor = 2;
        var exp = new Vector3D(2, 4, 6);
        assertVectorEquals(exp, vec.scale(factor));
    }
    @Test
    void scale_withZeroReturnsZeroVector(){
        var vec = new Vector3D(1, 2, 3);
        double factor = 0;
        var exp = new Vector3D(0, 0, 0);
        assertVectorEquals(exp, vec.scale(factor));
    }

    @Test
    void scale_withNegativeOneInvertsVector(){
        var vec = new Vector3D(1, 2, 3);
        double factor = -1;
        var exp = new Vector3D(-1, -2, -3);
        assertVectorEquals(exp, vec.scale(factor));
    }

    @Test
    void magnitude_returnsEuclideanLength2DCase(){
        var vec = new Vector3D(3, 4, 0);
        double exp = 5;
        assertEquals(exp, vec.magnitude(), Config.TOLERANCE);
    }

    @Test
    void magnitude_returnsEuclideanLength3DCase(){
        var vec = new Vector3D(1, 2, 2);
        double exp = 3;
        assertEquals(exp, vec.magnitude(), Config.TOLERANCE);
    }

    @Test
    void magnitude_ofZeroVectorIsZero(){
        var vec = new Vector3D(0, 0, 0);
        double exp = 0;
        assertEquals(exp, vec.magnitude(), Config.TOLERANCE);
    }
    
    @Test
    void normalize_returnsUnitVector(){
        var vec = new Vector3D(3, 4, 0);
        var exp = new Vector3D(0.6, 0.8, 0);
        assertVectorEquals(exp, vec.normalize());
    }

    @Test
    void normalize_resultHasMagnitudeOne(){
        var vec = new Vector3D(1, 2, 2);
        double exp = 1;
        assertEquals(exp, vec.normalize().magnitude(), Config.TOLERANCE);
    }
    @Test
    void invert_negatesAllComponents(){
        var vec = new Vector3D(1, -2, 3);
        var exp = new Vector3D(-1, 2, -3);
        assertVectorEquals(exp, vec.invert());
    }
    @Test
    void invert_twiceReturnsOriginalVector(){
        var vec = new Vector3D(1, -2, 3);
        assertVectorEquals(vec, vec.invert().invert());
    }
}
