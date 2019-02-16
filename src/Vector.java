/**
 * This class represents a 4D vector or homogenous point.
 *
 *
 */
public class Vector {

    double x, y, z, w; //values for the vector

    /**
     * Creates a new vector with the given values
     *
     * @param x x value of vector
     * @param y y value of vector
     * @param z z value of vector
     * @param w w value of vector
     */
    public Vector(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * No arg constructor that makes a vector with 0s for each value
     */
    public Vector() {
        this(0,0,0,0);
    }


    void normalize() {
        double magnitude = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
        this.x /= magnitude;
        this.y /= magnitude;
        this.z /= magnitude;
        this.w /= magnitude;
    }

    Vector subtract(Vector v) {
        return new Vector(
                this.x - v.x,
                this.y - v.y,
                this.z - v.z,
                this.w - v.w
        );
    }

    static Vector crossProduct(Vector u, Vector v) {
        return new Vector(
                u.y * v.z - u.z * v.y,
                u.z * v.x - u.x * v.z,
                u.x * v.y - u.y * v.x,
                0
        );
    }

    /**
     * Computes the dot product of two vectors. The
     * dot product is commutative.
     *
     * @param other the vector to be multiplied
     * @return the value of the dot product
     */
    double dotProduct(Vector other) {
        return x*other.x + y*other.y+ z*other.z + w*other.w;
    }

    public String toString() {
        return "[" + x + " " + y + " " + z + " " + w + "]";
    }
}
