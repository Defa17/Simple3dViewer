package com.cgvsu.render_engine;
import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;

import javax.vecmath.Point2f;

public class GraphicConveyor {

    private Matrix4f transformationMatrix;

    public GraphicConveyor(Matrix4f transformationMatrix) {
        this.transformationMatrix = transformationMatrix;
    }
    public GraphicConveyor() {

        this.transformationMatrix = new Matrix4f();
    }

    public Matrix4f getTransformationMatrix() {
        return transformationMatrix;
    }

    public void setTransformationMatrix(Matrix4f transformationMatrix) {
        this.transformationMatrix = transformationMatrix;
    }

    public static Matrix4f rotateScaleTranslate() {
        float[][] matrix = new float[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}};
        return new Matrix4f(matrix);
    }
    /**
     * Метод для масштабирования матрицы.
     * @params
     * sX, sY, sZ, - значения для изменения масштаба по каждой координате
     * **/
    public Matrix4f scale(float sX, float sY, float sZ) {
        Matrix4f scaleMatrix = new Matrix4f(new float[][]{
                {sX, 0, 0, 0},
                {0, sY, 0, 0},
                {0, 0, sZ, 0},
                {0, 0, 0, 1}
        });
        return transformationMatrix.multiply(scaleMatrix);
    }
    /**
     * Метод для универсального поворота матрицы вокруг осей.
     * @params
     * rX, rY, rZ - значения (в радианах) для поворота по каждой координате
     * **/

    public void rotate(float rX, float rY, float rZ) {
        float cosX = (float) Math.cos(rX);
        float sinX = (float) Math.sin(rX);
        float cosY = (float) Math.cos(rY);
        float sinY = (float) Math.sin(rY);
        float cosZ = (float) Math.cos(rZ);
        float sinZ = (float) Math.sin(rZ);

        Matrix4f rotationMatrix = new Matrix4f(new float[][]{
                {cosY * cosZ, -cosX * sinZ + sinX * sinY * cosZ, sinX * sinZ + cosX * sinY * cosZ, 0},
                {cosY * sinZ, cosX * cosZ + sinX * sinY * sinZ, -sinX * cosZ + cosX * sinY * sinZ, 0},
                {-sinY, sinX * cosY, cosX * cosY, 0},
                {0, 0, 0, 1}
        });

        transformationMatrix = transformationMatrix.multiply(rotationMatrix);
    }
    /**
     * Метод для параллельного переноса.
     * @params
     * tX, tY, tz - значения смещения по координатам
     * **/
    public void translate(float tX, float tY, float tZ) {
        Matrix4f translationMatrix = new Matrix4f(new float[][]{
                {1, 0, 0, tX},
                {0, 1, 0, tY},
                {0, 0, 1, tZ},
                {0, 0, 0, 1}
        });

        transformationMatrix = translationMatrix.multiply(translationMatrix);;
    }

    @Override
    public String toString() {
        return "AffineTransformation{" +
                "transformationMatrix=" + transformationMatrix +
                '}';
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f resultX = new Vector3f();
        Vector3f resultY = new Vector3f();
        Vector3f resultZ = new Vector3f();

        resultZ.sub(target, eye);
        resultX.cross(up, resultZ);
        resultY.cross(resultZ, resultX);

        resultX.normalize();
        resultY.normalize();
        resultZ.normalize();

        float[][] matrix = new float[][]{
                {resultX.x, resultY.x, resultZ.x, 0},
                {resultX.y, resultY.y, resultZ.y, 0},
                {resultX.z, resultY.z, resultZ.z, 0},
                {-resultX.dot(eye), -resultY.dot(eye), -resultZ.dot(eye), 1}};
        return new Matrix4f(matrix);
    }

    public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        float[][] result = new float[4][4];
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = 0;
            }

        }
        result[0][0] = tangentMinusOnDegree / aspectRatio;
        result[1][1] = tangentMinusOnDegree;
        result[2][2] = (farPlane + nearPlane) / (farPlane - nearPlane);
        result[2][3] = 1.0F;
        result[3][2] = 2 * (nearPlane * farPlane) / (nearPlane - farPlane);
        return new Matrix4f(result);
    }

    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector3f vertex) {
        final float[][] matrixData = matrix.getData();
        final float x = (vertex.x * matrixData[0][0]) + (vertex.y * matrixData[1][0]) + (vertex.z * matrixData[2][0]) + matrixData[3][0];
        final float y = (vertex.x * matrixData[0][1]) + (vertex.y * matrixData[1][1]) + (vertex.z * matrixData[2][1]) + matrixData[3][1];
        final float z = (vertex.x * matrixData[0][2]) + (vertex.y * matrixData[1][2]) + (vertex.z * matrixData[2][2]) + matrixData[3][2];
        final float w = (vertex.x * matrixData[0][3]) + (vertex.y * matrixData[1][3]) + (vertex.z * matrixData[2][3]) + matrixData[3][3];
        return new Vector3f(x / w, y / w, z / w);
    }
    //TODO: ПОСМОТРЕТЬ ДЛЯ ЧЕГО КЛАСС Point2f
    public static Point2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Point2f(vertex.x * width + width / 2.0F, -vertex.y * height + height / 2.0F);
    }
}
