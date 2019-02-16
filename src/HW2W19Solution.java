import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.event.*;

import java.io.*;
import java.util.*;

public class HW2W19Solution extends JPanel implements KeyListener {

    static int width;
    static int height;
    int imageSize;
    int[] pixels;

    int[] minx;
    int[] maxx;
    double[] minz;
    double[] maxz;



    double zbuffer[][];

    double vXrot = 0;
    double vYrot = 0;


    // to allow for initialization, checkZ turns off the z checking
    void drawPixel(int x, int y, double z, int r, int g, int b, boolean checkZ) {
        if (x < 0 || x >= width) {
            System.out.println("Bad x: "+x);
        } else if (y < 0 || y >= height) {
            System.out.println("Bad y: "+y);
        } else if (!checkZ || z < zbuffer[y][x]) {
            pixels[(height-y-1)*width*3+x*3] = r;
            pixels[(height-y-1)*width*3+x*3+1] = g;
            pixels[(height-y-1)*width*3+x*3+2] = b;
            zbuffer[y][x] = z;
        }
    }

    void drawTri(double x1,double y1, double z1,
                 double x2,double y2, double z2,
                 double x3,double y3, double z3,
                 int r,int g,int b){

        int  scrX1 = (int)Math.round(  ((x1+1)*(width-1)) /2.0),
                scrY1 = (int)Math.round(  ((y1+1)*(height-1)) /2.0),
                scrX2 = (int)Math.round(  ((x2+1)*(width-1)) /2.0),
                scrY2 = (int)Math.round(  ((y2+1)*(height-1))/2.0),
                scrX3 = (int)Math.round(  ((x3+1)*(width-1)) /2.0),
                scrY3 = (int)Math.round(  ((y3+1)*(height-1))/2.0);

        // initialize the scanline data structure (min/max x for each y-value)           
        for (int i = 0; i < height; i++) {
            minx[i] = Integer.MIN_VALUE;
            maxx[i] = Integer.MIN_VALUE;
        }


        drawLine(x1,y1,z1,x2,y2,z2,r,g,b);
        drawLine(x2,y2,z2,x3,y3,z3,r,g,b);
        drawLine(x3,y3,z3,x1,y1,z1,r,g,b);

        // paint the scanlines
        for (int y = 0; y < height; y++) {
            if (minx[y] != Integer.MIN_VALUE) {

                if (maxx[y] == minx[y]) {
                    drawPixel(minx[y], y, minz[y], r,g,b, true);
                } else {
                    double m = (maxz[y]-minz[y])/(maxx[y]-minx[y]);
                    double z = minz[y];
                    for(int x = minx[y]; x <= maxx[y]; x++, z += m) {
                        drawPixel(x, y, z, r,g,b, true);
                    }
                }
            }
        }

    }


    @SuppressWarnings("Duplicates")
    void drawLine(double x1, double y1, double z1,
                  double x2, double y2, double z2,
                  int r, int g, int b) {


        int  scrX1 = (int)Math.round(  ((x1+1)*(width-1)) /2.0),
                scrY1 = (int)Math.round(  ((y1+1)*(height-1)) /2.0),
                scrX2 = (int)Math.round(  ((x2+1)*(width-1)) /2.0),
                scrY2 = (int)Math.round(  ((y2+1)*(height-1))/2.0);

        if (Math.abs(scrY1-scrY2) >= Math.abs(scrX1-scrX2)) {  // steep lines

            if (scrY1 > scrY2) {
                int temp = scrX1;
                scrX1 = scrX2;
                scrX2 = temp;

                temp = scrY1;
                scrY1 = scrY2;
                scrY2 = temp;

                double tempf = z1;
                z1 = z2;
                z2 = tempf;
            }

            double m = (scrX2-scrX1)*1.0/(scrY2-scrY1);
            double mz = (z2-z1)/(scrY2-scrY1);

            double z = z1;
            double xfl = scrX1;
            int x;

            for (int y = scrY1; y <= scrY2; y++, z += mz, xfl += m) {

                x = (int) Math.round(xfl);
                drawPixel(x,y,z, r,g,b,true);
                System.out.println(y);
                if (minx[y] == Integer.MIN_VALUE) {
                    minx[y] = maxx[y] = x;
                    minz[y] = maxz[y] = z;
                } else {
                    if (x < minx[y]) {
                        minx[y] = x;
                        minz[y] = z;
                    }
                    if (x > maxx[y]) {
                        maxx[y] = x;
                        maxz[y] = z;
                    }
                }
            }
        } else {  // shallow lines

            if (scrX1 > scrX2) {
                int temp = scrX1;
                scrX1 = scrX2;
                scrX2 = temp;

                temp = scrY1;
                scrY1 = scrY2;
                scrY2 = temp;

                double tempz = z1;
                z1 = z2;
                z2 = tempz;
            }

            double m = (scrY2-scrY1)*1.0/(scrX2-scrX1);
            double mz = (z2-z1)/(scrX2-scrX1);

            double z = z1;
            double yfl = scrY1;
            int y;

            for (int x = scrX1; x <= scrX2; x++, z += mz, yfl += m) {

                y = (int)Math.round(yfl);
                drawPixel(x,y,z, r,g,b, true);

                if (minx[y] == Integer.MIN_VALUE) {
                    minx[y] = maxx[y] = x;
                    minz[y] = maxz[y] = z;
                } else {
                    if (x < minx[y]) {
                        minx[y] = x;
                        minz[y] = z;
                    }
                    if (x > maxx[y]) {
                        maxx[y] = x;
                        maxz[y] = z;
                    }
                }
            }
        }
    }

    Vector[] cubeVert = {
            new Vector(-0.5, 0.5, 0.5,1),   // front top left   0
            new Vector( 0.5, 0.5, 0.5,1),   // front top right  1
            new Vector(-0.5,-0.5, 0.5,1),   // front bottom left  2
            new Vector( 0.5,-0.5, 0.5,1),   // front bottom right  3
            new Vector(-0.5, 0.5,-0.5,1),   // back top left   4
            new Vector( 0.5, 0.5,-0.5,1),   // back top right   5
            new Vector(-0.5,-0.5,-0.5,1),   // back bottom left   6
            new Vector( 0.5,-0.5,-0.5,1),   // back bottom right   7
    };

    int[] cubeInd = {  // indices for 12 triangles, 2 per cube face
            0, 2, 1,  // front upper
            2, 3, 1,  // front lower
            5, 1, 3,  // right upper
            5, 3, 7,  // right lower
            4, 5, 7,  // back upper
            4, 7, 6,  // back lower
            0, 4, 6,  // left upper
            0, 6, 2,  // left lower
            0, 1, 5,  // top front
            0, 5, 4,  // top back
            3, 7, 2,  // bottom front
            2, 7, 6   // bottom back
    };
    int cube;

    @SuppressWarnings("Duplicates")
    void createImage() {

        int r = 0, g = 0, b = 0;

        Matrix ctm = Matrix.IDENTITY;

        Matrix lookAt = Matrix.IDENTITY;
        Matrix perspective = Matrix.IDENTITY;

        Matrix viewerXmatr = new Matrix(
                new Vector(1,0,0,0),
                new Vector(0,Math.cos(Math.toRadians(vXrot)),-Math.sin(Math.toRadians(vXrot)),0),
                new Vector(0,Math.sin(Math.toRadians(vXrot)),Math.cos(Math.toRadians(vXrot)),0),
                new Vector(0,0,0,1)
        );
        Matrix viewerYmatr = new Matrix(
                new Vector(Math.cos(Math.toRadians(vYrot)),0,Math.sin(Math.toRadians(vYrot)),0),
                new Vector(0,1,0,0),
                new Vector(-Math.sin(Math.toRadians(vYrot)),0,Math.cos(Math.toRadians(vYrot)),0),
                new Vector(0,0,0,1)
        );

        Matrix viewerMat = Matrix.multiply(viewerYmatr,viewerXmatr);
        // Instructions for HW2 don't indicate the order for these matrices,
        // so the following line of code is just as good as the one above
        // Matrix viewerMat = Matrix.multiply(viewerXmatr,viewerYmatr);


        Scanner input = getFile();
        while (input.hasNext()) {
            String command = input.next();
            if (command.equals("DIM")){
                width = input.nextInt() * 2;
                height = input.nextInt() * 2;

                minx = new int[height];
                maxx = new int[height];

                minz = new double[height];
                maxz = new double[height];

                imageSize = width * height;
                pixels = new int[imageSize * 3];
                zbuffer = new double[height][];
                // init the z buffer
                for (int row = 0; row < height; row++) {
                    zbuffer[row] = new double[width];
                }
                // init the background color
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        drawPixel(x,y,Double.POSITIVE_INFINITY,255,255,255, false);
                    }
                }



            } else if (command.equals("LINE")){
                float x1,y1,z1,x2,y2,z2;
                x1 = input.nextFloat();
                y1 = input.nextFloat();
                z1 = input.nextFloat();
                x2 = input.nextFloat();
                y2 = input.nextFloat();
                z2 = input.nextFloat();
                Vector v1 = Matrix.multiply(viewerMat,Matrix.multiply(ctm,new Vector(x1,y1,z1,1)));
                Vector v2 = Matrix.multiply(viewerMat,Matrix.multiply(ctm,new Vector(x2,y2,z2,1)));

                drawLine(v1.x/v1.w,v1.y/v1.w,v1.z/v1.w,
                        v2.x/v2.w,v2.y/v2.w,v2.z/v2.w,
                        r,g,b);

            } else if (command.equals("RGB")){
                r = Math.round(input.nextFloat()*255);
                g = Math.round(input.nextFloat()*255);
                b = Math.round(input.nextFloat()*255);

            } else if (command.equals("TRI")){
                float x1,y1,z1,x2,y2,z2,x3,y3,z3;
                x1 = input.nextFloat();
                y1 = input.nextFloat();
                z1 = input.nextFloat();
                x2 = input.nextFloat();
                y2 = input.nextFloat();
                z2 = input.nextFloat();
                x3 = input.nextFloat();
                y3 = input.nextFloat();
                z3 = input.nextFloat();

                Vector v1 = Matrix.multiply(perspective, Matrix.multiply(lookAt, Matrix.multiply(viewerMat,Matrix.multiply(ctm,new Vector(x1,y1,z1,1)))));
                Vector v2 = Matrix.multiply(perspective, Matrix.multiply(lookAt, Matrix.multiply(viewerMat,Matrix.multiply(ctm,new Vector(x2,y2,z2,1)))));
                Vector v3 = Matrix.multiply(perspective, Matrix.multiply(lookAt, Matrix.multiply(viewerMat,Matrix.multiply(ctm,new Vector(x3,y3,z3,1)))));

                drawTri(v1.x/v1.w, v1.y/v1.w, v1.z/v1.w,
                        v2.x/v2.w, v2.y/v2.w, v2.z/v2.w,
                        v3.x/v3.w, v3.y/v3.w, v3.z/v3.w,
                        r,g,b);

            } else if (command.equals("LOAD_IDENTITY_MATRIX")){
                ctm = Matrix.IDENTITY;

            } else if (command.equals("SCALE")){
                Matrix trans = new Matrix(
                        new Vector(input.nextFloat(),0,0,0),
                        new Vector(0,input.nextFloat(),0,0),
                        new Vector(0,0,input.nextFloat(),0),
                        new Vector(0,0,0,1)
                );
                ctm = Matrix.multiply(trans,ctm);

            } else if (command.equals("ROTATEX")){
                double th = (2*Math.PI*input.nextFloat())/360;
                Matrix rotx = new Matrix(
                        new Vector(1,0,0,0),
                        new Vector(0,Math.cos(th),-Math.sin(th),0),
                        new Vector(0,Math.sin(th),Math.cos(th),0),
                        new Vector(0,0,0,1)
                );
                ctm = Matrix.multiply(rotx,ctm);

            } else if (command.equals("ROTATEY")){
                double th = (2*Math.PI*input.nextFloat())/360;
                Matrix roty = new Matrix(
                        new Vector(Math.cos(th),0,Math.sin(th),0),
                        new Vector(0,1,0,0),
                        new Vector(-Math.sin(th),0,Math.cos(th),0),
                        new Vector(0,0,0,1)
                );
                ctm = Matrix.multiply(roty,ctm);

            } else if (command.equals("ROTATEZ")){
                double th = (2*Math.PI*input.nextFloat())/360;
                Matrix rotz = new Matrix(
                        new Vector(Math.cos(th),-Math.sin(th),0,0),
                        new Vector(Math.sin(th),Math.cos(th),0,0),
                        new Vector(0,0,1,0),
                        new Vector(0,0,0,1)
                );
                ctm = Matrix.multiply(rotz,ctm);

            } else if (command.equals("TRANSLATE")){
                Matrix trans = new Matrix(
                        new Vector(1,0,0,input.nextFloat()),
                        new Vector(0,1,0,input.nextFloat()),
                        new Vector(0,0,1,input.nextFloat()),
                        new Vector(0,0,0,1)
                );
                ctm = Matrix.multiply(trans,ctm);

            } else if (command.equals("SOLID_CUBE")){

                for (int i = 0; i < 36; i += 3) {   // making 12 triangles, two for each cube face
                    Vector v1 = Matrix.multiply(viewerMat,Matrix.multiply(ctm,cubeVert[cubeInd[i]]));
                    Vector v2 = Matrix.multiply(viewerMat,Matrix.multiply(ctm,cubeVert[cubeInd[i+1]]));
                    Vector v3 = Matrix.multiply(viewerMat,Matrix.multiply(ctm,cubeVert[cubeInd[i+2]]));

                    drawTri(v1.x/v1.w,v1.y/v1.w,v1.z/v1.w,
                            v2.x/v2.w,v2.y/v2.w,v2.z/v2.w,
                            v3.x/v3.w,v3.y/v3.w,v3.z/v3.w,
                            r,g,b);
                }

            } else if (command.equals("WIREFRAME_CUBE")){
                Vector[] v1 = {  // 12 edges of the cube, 2 endpoints each = 24 vertices
                        new Vector(-0.5, 0.5, 0.5, 1),new Vector( 0.5, 0.5, 0.5, 1),  // front top
                        new Vector(-0.5,-0.5, 0.5, 1),new Vector( 0.5,-0.5, 0.5, 1),  // front bottom
                        new Vector(-0.5, 0.5, 0.5, 1),new Vector(-0.5,-0.5, 0.5, 1),  // front left
                        new Vector( 0.5, 0.5, 0.5, 1),new Vector( 0.5,-0.5, 0.5, 1),  // front right
                        new Vector(-0.5, 0.5,-0.5, 1),new Vector( 0.5, 0.5,-0.5, 1),  // back top
                        new Vector(-0.5,-0.5,-0.5, 1),new Vector( 0.5,-0.5,-0.5, 1),  // back bottom
                        new Vector(-0.5, 0.5,-0.5, 1),new Vector(-0.5,-0.5,-0.5, 1),  // back left
                        new Vector( 0.5, 0.5,-0.5, 1),new Vector( 0.5,-0.5,-0.5, 1),  // back right
                        new Vector(-0.5, 0.5, 0.5, 1),new Vector(-0.5, 0.5,-0.5, 1),  // thru top left
                        new Vector( 0.5, 0.5, 0.5, 1),new Vector( 0.5, 0.5,-0.5, 1),  // thru top right
                        new Vector(-0.5,-0.5, 0.5, 1),new Vector(-0.5,-0.5,-0.5, 1),  // thru bottom left
                        new Vector( 0.5,-0.5, 0.5, 1),new Vector( 0.5,-0.5,-0.5, 1)   // thru bottom right
                };

                for (int i = 0; i < 12; i++) {
                    Vector pt1 = Matrix.multiply(viewerMat,Matrix.multiply(ctm,v1[2*i]));
                    Vector pt2 = Matrix.multiply(viewerMat,Matrix.multiply(ctm,v1[2*i+1]));
                    drawLine(pt1.x/pt1.w,pt1.y/pt1.w,pt1.z/pt1.w,
                            pt2.x/pt2.w,pt2.y/pt2.w,pt2.z/pt2.w,
                            r,g,b);
                }

            } else if (command.equals("ORTHO")) {
                double left = input.nextFloat();
                double right = input.nextFloat();
                double top = input.nextFloat();
                double btm = input.nextFloat();
                double near = input.nextFloat();
                double far = input.nextFloat();

                perspective = new Matrix(
                        new Vector(2 / (right - left), 0, 0, 0),
                        new Vector(0, 2 / (top - btm), 0, 0),
                        new Vector(0, 0, -2 / (far - near), 0),
                        new Vector(-(right + left) / (right - left), -(top + btm) / (top - btm), -(far + near) / (far - near), 1)
                );

            } else if (command.equals("FRUSTUM")) {
                double left = input.nextFloat();
                double right = input.nextFloat();
                double top = input.nextFloat();
                double btm = input.nextFloat();
                double near = input.nextFloat();
                double far = input.nextFloat();

                perspective = new Matrix(
                        new Vector(2 * near / (right - left), 0, 0, 0),
                        new Vector(0, 2 * near / (top  - btm), 0, 0),
                        new Vector(0, 0, -(far + near) / (far - near), -1),
                        new Vector(-near * (right + left) / (right - left), -near * (top + btm) / (top - btm), 2 * far * near / (near - far), 0)
                );


            } else if (command.equals("LOOKAT")) {
                Vector eye = new Vector(input.nextFloat(), input.nextFloat(), input.nextFloat(), 0);
                Vector center = new Vector(input.nextFloat(), input.nextFloat(), input.nextFloat(), 0);
                Vector up = new Vector(input.nextFloat(), input.nextFloat(), input.nextFloat(), 0);

                Vector n = eye.subtract(center);
                n.normalize();

                Vector u = Vector.crossProduct(up, n);
                u.normalize();

                Vector v = Vector.crossProduct(n, u);
                v.normalize();

                double tx = u.dotProduct(eye);
                double ty = v.dotProduct(eye);
                double tz = n.dotProduct(eye);

                lookAt = new Matrix(
                        new Vector(u.x, v.x, n.x, 0),
                        new Vector(u.y, v.y, n.y, 0),
                        new Vector(u.z, v.z, n.z, 0),
                        new Vector(tx, ty, tz, 1)
                );

                //Apply after transformations

            } else if (command.equals("LIGHT_DIRECTION")) {
                Vector source = new Vector(
                        input.nextFloat(),
                        input.nextFloat(),
                        input.nextFloat(),
                        0
                );
                //Calculate shading factor.
                //TODO implement triangle normals first.
                /*
                Color = Ambient(rgb) + directed(rgb * sf)
                Shading factor = dot product of normalized light and triangle vectors.
                 */
                source.normalize();

            }
        }
    }

    // ------------------------------------  MOST MODIFICATIONS  -------------------------------------------------------


    /**
     * Creates a new pixel array with anti aliasing.
     * @return Array of pixels with AA applied.
     */
    @SuppressWarnings("Duplicates")
    private int[] applyAntiAliasing() {
        int w = width / 2;
        int h = height / 2;
        int imageSize = w * h;
        int[] p = new int[imageSize * 3];
        System.out.println(p.length);
        for (int x = 0; x < width; x += 2) {
            for (int y = 0; y < height; y += 2) {
                int avgR = aliasAverage(
                        pixels[(height-y-1) * width * 3 + x * 3],
                        pixels[(height-y-1) * width * 3 + x * 3 + 3],
                        pixels[(height-y-2) * width * 3 + x * 3],
                        pixels[(height-y-2) * width * 3 + x * 3 + 3]

                );
                int avgG = aliasAverage(
                        pixels[(height-y-1) * width*3 + x * 3 + 1],
                        pixels[(height-y-1) * width*3 + x * 3 + 4],
                        pixels[(height-y-2) * width*3 + x * 3 + 1],
                        pixels[(height-y-2) * width*3 + x * 3 + 4]
                );
                int avgB = aliasAverage(
                        pixels[(height-y-1) * width*3 + x * 3 + 2],
                        pixels[(height-y-1) * width*3 + x * 3 + 5],
                        pixels[(height-y-2) * width*3 + x * 3 + 2],
                        pixels[(height-y-2) * width*3 + x * 3 + 5]
                );
                p[(h - y / 2 - 1) * w * 3 + (x / 2 ) * 3] = avgR;
                p[(h - y / 2 - 1) * w * 3 + (x / 2 ) * 3 + 1] = avgG;
                p[(h - y / 2 - 1) * w * 3 + (x / 2 ) * 3 + 2] = avgB;
            }
        }
        return p;
    }

    // Calculates the average color value for anti aliasing.
    private int aliasAverage(int c0, int c1, int c2, int c3) {
        return (c0 + c1 + c2 + c3) / 4;
    }


    // ------------------------------------------------- END MODIFICATIONS ---------------------------------------

    public HW2W19Solution() {
        setPreferredSize(new Dimension(512,512));
        setFocusable(true);
        addKeyListener(this);
    }


    /**
     * Modified so that pixel array with anti aliasing is drawn.
     * @param g Graphics
     */
    public void paintComponent(Graphics g) {
        createImage();
        BufferedImage image = new BufferedImage(width / 2, height / 2, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = image.getRaster();
        raster.setPixels(0, 0, width / 2, height / 2, this.applyAntiAliasing());
        g.drawImage(image, 0, 0, null);
    }


    public static void main(String args[]) {
        JFrame frame = new JFrame("Homework 2 Solution, Winter 2019");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        selectFile();

        JPanel rootPane = new HW2W19Solution();
        getDim(rootPane);
        rootPane.setPreferredSize(new Dimension(width,height));

        frame.getContentPane().add(rootPane);
        frame.pack();
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
    }

    static File selectedFile = null;


    static private void selectFile() {
        int approve; //return value from JFileChooser indicates if the user hit cancel

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));

        approve = chooser.showOpenDialog(null);
        if (approve != JFileChooser.APPROVE_OPTION) {
            System.exit(0);
        } else {
            selectedFile = chooser.getSelectedFile();
        }
    }

    static private Scanner getFile() {
        Scanner input = null;
        System.out.println(selectedFile);
        try {
            input = new Scanner(selectedFile);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "There was an error with the file you chose",
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
        return input;
    }

    static void getDim(JPanel rootPane) {
        Scanner input = getFile();

        String command = input.next();
        if (command.equals("DIM")){
            width = input.nextInt();
            height = input.nextInt();
            rootPane.setPreferredSize(new Dimension(width,height));
        }
    }


    static final double ANGLE_INCREMENT = 3;

    /**
     * Rotates the image by the ANGLE_INCREMENT.
     * @param e the key pressed
     */
    public void keyPressed(KeyEvent e){

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            vXrot  -= ANGLE_INCREMENT;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            vXrot  += ANGLE_INCREMENT;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            vYrot  -= ANGLE_INCREMENT;
        }  else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            vYrot  += ANGLE_INCREMENT;
        }
        repaint();
    }

    /**
     * Not used
     */
    public void keyReleased(KeyEvent e){
    }

    /**
     * Key typed event - not used
     */
    public void keyTyped(KeyEvent e) {
    }

}