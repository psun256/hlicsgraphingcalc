import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.Animator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/*
 * renderer for the graphs
 * uses OpenGL 3.3 core profile (not on the school computers unfortunately)
 * we use JOGL to provide the function bindings, because it has integration with Swing and AWT components,
 * unlike LWJGL or other libraries
 *
 * the renderer is a JPanel, so it can be added to a JFrame or other container
 * inside the renderer we have a GLCanvas, which holds the opengl context to be used
 *
 * acts as a frontend for the marching cubes algorithm. it takes in a list of triangles from the
 * marching cubes algorithm, and provides a way to view them
 */
public class Renderer extends JPanel implements GLEventListener {
    private final float MOUSE_SENSITIVITY = 0.01f;
    private final float ZOOM_SENSITIVITY = 0.5f;
    private final float MIN_ZOOM = 1.0f;
    private final float MAX_ZOOM = 50.0f;
    private final float MAX_PITCH = (float) Math.PI / 2.0f;
    private final float MIN_PITCH = -(float) Math.PI / 2.0f;
    private final float MAJOR_TICK_SIZE = 0.02f;
    private final float MINOR_TICK_SIZE = 0.01f;
    String fnVertexSource, fnFragmentSource, axesVertexSource, axesFragmentSource;
    private GLCanvas canvas;
    private int programId1, programId2;
    private int[] vao1, vao2;
    private float[] vertices;
    private float[] axesVertices;
    private double x1, y1, z1, x2, y2, z2;
    private vec3 center, bounds;
    private Matrix4 mvpMatrix;
    private float cameraZoom = 5.0f, cameraYaw, cameraPitch;
    private float scaleX, scaleY, scaleZ;
    MouseWheelListener mouseWheelListener = new MouseWheelListener() {
        @Override
        public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
            cameraZoom += mouseWheelEvent.getWheelRotation() * ZOOM_SENSITIVITY;
            cameraZoom = Math.max(cameraZoom, MIN_ZOOM);
            cameraZoom = Math.min(cameraZoom, MAX_ZOOM);
            reshape(canvas, 0, 0, canvas.getWidth(), canvas.getHeight());
        }
    };
    private int lastMouseX, lastMouseY;
    MouseMotionListener mouseDrag = new MouseMotionListener() {
        @Override
        public void mouseDragged(MouseEvent e) {
            cameraYaw += (e.getX() - lastMouseX) * MOUSE_SENSITIVITY;
            cameraPitch += (e.getY() - lastMouseY) * MOUSE_SENSITIVITY;
            cameraPitch = Math.max(cameraPitch, MIN_PITCH);
            cameraPitch = Math.min(cameraPitch, MAX_PITCH);
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            reshape(canvas, 0, 0, canvas.getWidth(), canvas.getHeight());
        }
        
        @Override
        public void mouseMoved(MouseEvent e) {
            lastMouseX = e.getX();
            lastMouseY = e.getY();
        }
        
    };
    private Animator animator;
    
    public Renderer() {
        super();
    }
    
    public Renderer(double x1, double y1, double z1, double x2, double y2, double z2, ArrayList<Float> vertices) throws IOException {
        super();
        
        // load the vertices from the marching cubes algorithm, as well as doing some preprocessing
        // notably, we interlace the vertices with their normals, so that the vertex shader can send them
        // to the fragment shader through a varying vec3. this lets the fragment shader figure out the colors, and
        // importantly, the gridlines
        this.vertices = new float[vertices.size() * 2];
        for (int i = 0; i < vertices.size(); i += 9) {
            vec3 v1 = new vec3(vertices.get(i), vertices.get(i + 1), vertices.get(i + 2));
            vec3 v2 = new vec3(vertices.get(i + 3), vertices.get(i + 4), vertices.get(i + 5));
            vec3 v3 = new vec3(vertices.get(i + 6), vertices.get(i + 7), vertices.get(i + 8));
            vec3 normal = vec3.cross(vec3.subtract(v2, v1), vec3.subtract(v3, v1)).normalize();
            this.vertices[i * 2] = vertices.get(i);
            this.vertices[i * 2 + 1] = vertices.get(i + 1);
            this.vertices[i * 2 + 2] = vertices.get(i + 2);
            this.vertices[i * 2 + 3] = (float) normal.x;
            this.vertices[i * 2 + 4] = (float) normal.y;
            this.vertices[i * 2 + 5] = (float) normal.z;
            this.vertices[i * 2 + 6] = vertices.get(i + 3);
            this.vertices[i * 2 + 7] = vertices.get(i + 4);
            this.vertices[i * 2 + 8] = vertices.get(i + 5);
            this.vertices[i * 2 + 9] = (float) normal.x;
            this.vertices[i * 2 + 10] = (float) normal.y;
            this.vertices[i * 2 + 11] = (float) normal.z;
            this.vertices[i * 2 + 12] = vertices.get(i + 6);
            this.vertices[i * 2 + 13] = vertices.get(i + 7);
            this.vertices[i * 2 + 14] = vertices.get(i + 8);
            this.vertices[i * 2 + 15] = (float) normal.x;
            this.vertices[i * 2 + 16] = (float) normal.y;
            this.vertices[i * 2 + 17] = (float) normal.z;
        }
        
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        
        // stores information about the volume being rendered, so that the camera can be positioned correctly
        center = new vec3((x1 + x2) / 2, (y1 + y2) / 2, (z1 + z2) / 2);
        bounds = new vec3(x2 - x1, y2 - y1, z2 - z1);
        
        cameraYaw = (float) Math.PI / 4;
        cameraPitch = (float) Math.PI / 8;
        
        //viewMatrix = new Matrix4();
        //viewMatrix.rotate((float)Math.PI/8, 1, 0, 0);
        //viewMatrix.rotate(-(float)Math.PI/5, 0, 1, 0);
        //viewMatrix.rotate(10, 0, 1, 0);
        //viewMatrix.rotate(10, 0, 0, 1);
        //viewMatrix.scale(0.3f, 0.3f, 0.3f);
        //projectionMatrix = new Matrix4();
        //projectionMatrix.makeOrtho(-1, 1, -1, 1, -1, 1);
        
        // uses the Model View Projection matrices idea to position the camera, and render the model
        // in the correct place
        // first the model matrix. the model matrix is responsible for converting the model's local coordinates into
        // the global coordinate space. to make things easier, I extended the functionality of the matrix to also
        // normalize the model's vertices to the range -0.5 to 0.5, so that the model is always rendered in the same,
        // and to make the remaining matrices easier to calculate
        
        // note that since the MVP system works is as such:
        // V_final = M_proj * M_view * M_model * V_local
        // we need to apply the transformations / matrices in reverse order
        Matrix4 modelMatrix = new Matrix4(); // translate and rescale model down
        modelMatrix.scale((float) (2f / bounds.x), (float) (2f / bounds.y), (float) (2f / bounds.z));
        modelMatrix.translate((float) -center.x, (float) -center.y, (float) -center.z);
        
        // the view matrix rotates the model so that all the coordinates are in the camera's coordinate space.
        Matrix4 viewMatrix = new Matrix4(); // camera orientation
        viewMatrix.rotate(cameraPitch, 1, 0, 0);
        viewMatrix.rotate(cameraYaw, 0, 1, 0);
        
        // we are using an orthographic projection. this means that depth information is useless to us
        // the reason I did this is because it makes it easier to examine the rendered model
        Matrix4 projectionMatrix = new Matrix4();
        
        // set the left and right bounds of the orthographic projection to be the same aspect ratio as the window
        // so that the model still appears normal
        // near and far clipping planes don't matter for orthographic projection, so we set them to arbitrary values
        // that are big enough to encompass the model all the time
        float width = 16.0f / 9.0f * cameraZoom;
        float height = cameraZoom;
        projectionMatrix.makeOrtho(-width / 2, width / 2, -height / 2, height / 2, -2, 2);
        
        // the final matrix is the product of all the matrices
        mvpMatrix = new Matrix4();
        mvpMatrix.multMatrix(projectionMatrix);
        mvpMatrix.multMatrix(viewMatrix);
        mvpMatrix.multMatrix(modelMatrix);
        
        // read the vertex shader source and fragment shader sources from ./shaders/
        fnVertexSource = Files.readString(Paths.get("shaders/fn_vertex.vsh"));
        fnFragmentSource = Files.readString(Paths.get("shaders/fn_fragment.fsh"));
        axesVertexSource = Files.readString(Paths.get("shaders/axes_vertex.vsh"));
        axesFragmentSource = Files.readString(Paths.get("shaders/axes_fragment.fsh"));
        
        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        canvas.addMouseWheelListener(mouseWheelListener);
        canvas.addMouseMotionListener(mouseDrag);
        
        animator = new Animator(canvas);
        
        this.setLayout(new BorderLayout());
        this.add(canvas, BorderLayout.CENTER);
    }
    
    // code used for testing this class directly, without having to integrate into the rest of the program
    public static void main(String[] args) throws IOException {
        //MarchingCubes mc = new MarchingCubes(new Expression("(3x^4-3x^2+1.7y^2)^2(y^2-1)^2+(3y^4-3y^2+1.7z^2)^2(z^2-1)^2+(3z^4-3z^2+1.7x^2)^2(x^2-1)^2-0.02"), -1.1, -1.1, -1.1, 1.1, 1.1, 1.1);
        //MarchingCubes mc = new MarchingCubes(new Expression("x^2y^2+y^2z^2+z^2x^2-xyz"), -1.1, -1.1, -1.1, 1.1, 1.1, 1.1);
        //MarchingCubes mc = new MarchingCubes(new Expression("sin(x+z)-cos(xz)+1-4y"), -5, -5, -5, 5, 5, 5);
        //MarchingCubes mc = new MarchingCubes(new Expression("xsin(z)+ycos(x)"), -5, -5, -5, 5, 5, 5);
        //MarchingCubes mc = new MarchingCubes(new Expression("1/x^2 - 1/z^2 + 1/y^2"), -5, -5, -5, 5, 5, 5);
        MarchingCubes mc = new MarchingCubes(new Expression("sin(x)*cos(y) + sin(y)*cos(z) + sin(z)*cos(x)"), -5, -5, -5, 5, 5, 5);
        mc.generateScalarField();
        ArrayList<Float> vertices = mc.extractMesh();
        System.out.println("Vertices: " + vertices.size() / 3);
        
        JFrame frame = new JFrame("Marching Cubes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        //Renderer r = new Renderer(-1.1, -1.1, -1.1, 1.1, 1.1, 1.1, vertices);
        Renderer r = new Renderer(-5, -5, -5, 5, 5, 5, vertices);
        r.setVisible(true);
        
        frame.getContentPane().add(r);
        frame.setVisible(true);
    }
    
    // utility function to compile shaders, and print any errors that occur
    private int compileShader(GL3 gl, int type, String source) {
        int shaderId = gl.glCreateShader(type);
        gl.glShaderSource(shaderId, 1, new String[]{source}, null);
        gl.glCompileShader(shaderId);
        int[] logLength = new int[1];
        gl.glGetShaderiv(shaderId, GL3.GL_INFO_LOG_LENGTH, logLength, 0);
        if (logLength[0] > 0) {
            byte[] log = new byte[logLength[0]];
            gl.glGetShaderInfoLog(shaderId, logLength[0], null, 0, log, 0);
            System.out.println(new String(log));
        }
        return shaderId;
    }
    
    // automatically called to initialize an opengl context
    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL3 gl = glAutoDrawable.getGL().getGL3();
        
        // background color
        gl.glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
        
        // depth testing. important to ensure that faces that are blocked by faces in front of them are not drawn
        gl.glEnable(GL3.GL_DEPTH_TEST);
        
        // compile the shaders for displaying the function (gridlines, colors, etc)
        int fnVertexShaderId = compileShader(gl, GL3.GL_VERTEX_SHADER, fnVertexSource);
        int fnFragmentShaderId = compileShader(gl, GL3.GL_FRAGMENT_SHADER, fnFragmentSource);
        
        // create a shader program
        programId1 = gl.glCreateProgram();
        gl.glAttachShader(programId1, fnVertexShaderId);
        gl.glAttachShader(programId1, fnFragmentShaderId);
        gl.glLinkProgram(programId1);
        
        // vertex buffer object to store all of the vertex information
        int[] vbo1 = new int[1];
        gl.glGenBuffers(1, vbo1, 0);
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo1[0]);
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, FloatBuffer.wrap(vertices), GL3.GL_STATIC_DRAW);
        
        // vertex array object to tell opengl how to read the vertex buffer object
        vao1 = new int[1];
        gl.glGenVertexArrays(1, vao1, 0);
        gl.glBindVertexArray(vao1[0]);
        
        // attribute 0 will be the vertex positions, since our data is stored as x1, y1, z1, nx1, ny1, nz1, x2, y2, z2, nx2, ny2, nz2, etc
        // 3 elements, jumps of 6 elements, starting at the beginning
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 6 * Float.BYTES, 0);
        
        // attribute 1 will be the normal vectors
        // 3 elements, jumps of 6 elements, starting at the 3rd element (offset of 3 * Float.BYTES)
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL3.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        
        // unbind the vertex array object we are currently using, so it doesn't get modified by accident
        gl.glBindVertexArray(0);
        
        // compile the shaders for displaying the axes (lines)
        int axesVertexShaderId = compileShader(gl, GL3.GL_VERTEX_SHADER, axesVertexSource);
        int axesFragmentShaderId = compileShader(gl, GL3.GL_FRAGMENT_SHADER, axesFragmentSource);
        
        // create a shader program
        programId2 = gl.glCreateProgram();
        gl.glAttachShader(programId2, axesVertexShaderId);
        gl.glAttachShader(programId2, axesFragmentShaderId);
        gl.glLinkProgram(programId2);
        
        // calculate the scale on the axes. we want the axes to be 1/10th the size of the function at smallest
        scaleX = (float) Math.pow(10, Math.floor(Math.log10(bounds.x)));
        scaleY = (float) Math.pow(10, Math.floor(Math.log10(bounds.y)));
        scaleZ = (float) Math.pow(10, Math.floor(Math.log10(bounds.z)));
        
        // the vertices for axes. we also interlace them with more data, but instead of normals, it's the color
        // of the line
        float[] axesBox = new float[]{
            // z axis, blue
            (float) x1, (float) y1, (float) z1, 0, 0, 1,
            (float) x1, (float) y1, (float) z2, 0, 0, 1,
            
            // black
            (float) x1, (float) y1, (float) z2, 0, 0, 0,
            (float) x1, (float) y2, (float) z2, 0, 0, 0,
            (float) x1, (float) y2, (float) z2, 0, 0, 0,
            (float) x1, (float) y2, (float) z1, 0, 0, 0,
            
            // y axis, green
            (float) x1, (float) y2, (float) z1, 0, 1, 0,
            (float) x1, (float) y1, (float) z1, 0, 1, 0,
            
            // black
            (float) x2, (float) y1, (float) z1, 0, 0, 0,
            (float) x2, (float) y1, (float) z2, 0, 0, 0,
            (float) x2, (float) y1, (float) z2, 0, 0, 0,
            (float) x2, (float) y2, (float) z2, 0, 0, 0,
            (float) x2, (float) y2, (float) z2, 0, 0, 0,
            (float) x2, (float) y2, (float) z1, 0, 0, 0,
            (float) x2, (float) y2, (float) z1, 0, 0, 0,
            (float) x2, (float) y1, (float) z1, 0, 0, 0,
            
            // x axis, red
            (float) x1, (float) y1, (float) z1, 1, 0, 0,
            (float) x2, (float) y1, (float) z1, 1, 0, 0,
            
            // black
            (float) x1, (float) y1, (float) z2, 0, 0, 0,
            (float) x2, (float) y1, (float) z2, 0, 0, 0,
            (float) x1, (float) y2, (float) z1, 0, 0, 0,
            (float) x2, (float) y2, (float) z1, 0, 0, 0,
            (float) x1, (float) y2, (float) z2, 0, 0, 0,
            (float) x2, (float) y2, (float) z2, 0, 0, 0,
        };
        
        // make new arraylist of floats called axes with the same values as axesVertices
        ArrayList<Float> axes = new ArrayList<Float>();
        for (int i = 0; i < axesBox.length; i++) {
            axes.add(axesBox[i]);
        }
        
        // create tick marks on the axes
        // along each axis, find all the multiples of the major scale
        // create a tick mark diagonal to the axis, pointing away, and make it's size proportional to the scale (ratio is MAJOR_TICK_SIZE)
        // same for the minor ticks, but use MINOR_TICK_SIZE
        // for the minor ticks, we need to find the multiples of the minor scale
        // color them with the same color as the axis they're on
        // add them to the axes arraylist
        
        // x axis major
        float tickX = (float) Math.ceil(x1 / scaleX) * scaleX;
        while (tickX < x1) {
            tickX += scaleX;
        }
        while (tickX < x2) {
            axes.add(tickX);
            axes.add((float) y1);
            axes.add((float) z1);
            axes.add(1f);
            axes.add(0f);
            axes.add(0f);
            axes.add(tickX);
            axes.add((float) y1 - MAJOR_TICK_SIZE * (float) bounds.y);
            axes.add((float) z1 - MAJOR_TICK_SIZE * (float) bounds.z);
            axes.add(1f);
            axes.add(0f);
            axes.add(0f);
            tickX += scaleX;
        }
        
        // y axis major
        float tickY = (float) Math.ceil(y1 / scaleY) * scaleY;
        while (tickY < y1) {
            tickY += scaleY;
        }
        while (tickY < y2) {
            axes.add((float) x1);
            axes.add(tickY);
            axes.add((float) z1);
            axes.add(0f);
            axes.add(1f);
            axes.add(0f);
            axes.add((float) x1 - MAJOR_TICK_SIZE * (float) bounds.x);
            axes.add(tickY);
            axes.add((float) z1 - MAJOR_TICK_SIZE * (float) bounds.z);
            axes.add(0f);
            axes.add(1f);
            axes.add(0f);
            tickY += scaleY;
        }
        
        // z axis major
        float tickZ = (float) Math.ceil(z1 / scaleZ) * scaleZ;
        while (tickZ < z1) {
            tickZ += scaleZ;
        }
        while (tickZ < z2) {
            axes.add((float) x1);
            axes.add((float) y1);
            axes.add(tickZ);
            axes.add(0f);
            axes.add(0f);
            axes.add(1f);
            axes.add((float) x1 - MAJOR_TICK_SIZE * (float) bounds.x);
            axes.add((float) y1 - MAJOR_TICK_SIZE * (float) bounds.y);
            axes.add(tickZ);
            axes.add(0f);
            axes.add(0f);
            axes.add(1f);
            tickZ += scaleZ;
        }
        
        // x axis minor
        tickX = (float) Math.ceil(x1 / (scaleX / 10)) * (scaleX / 10);
        while (tickX < x1) {
            tickX += (scaleX / 10);
        }
        while (tickX < x2) {
            axes.add(tickX);
            axes.add((float) y1);
            axes.add((float) z1);
            axes.add(1f);
            axes.add(0f);
            axes.add(0f);
            axes.add(tickX);
            axes.add((float) y1 - MINOR_TICK_SIZE * (float) bounds.y);
            axes.add((float) z1 - MINOR_TICK_SIZE * (float) bounds.z);
            axes.add(1f);
            axes.add(0f);
            axes.add(0f);
            tickX += (scaleX / 10);
        }
        
        // y axis minor
        tickY = (float) Math.ceil(y1 / (scaleY / 10)) * (scaleY / 10);
        while (tickY < y1) {
            tickY += (scaleY / 10);
        }
        while (tickY < y2) {
            axes.add((float) x1);
            axes.add(tickY);
            axes.add((float) z1);
            axes.add(0f);
            axes.add(1f);
            axes.add(0f);
            axes.add((float) x1 - MINOR_TICK_SIZE * (float) bounds.x);
            axes.add(tickY);
            axes.add((float) z1 - MINOR_TICK_SIZE * (float) bounds.z);
            axes.add(0f);
            axes.add(1f);
            axes.add(0f);
            tickY += (scaleY / 10);
        }
        
        // z axis minor
        tickZ = (float) Math.ceil(z1 / (scaleZ / 10)) * (scaleZ / 10);
        while (tickZ < z1) {
            tickZ += (scaleZ / 10);
        }
        while (tickZ < z2) {
            axes.add((float) x1);
            axes.add((float) y1);
            axes.add(tickZ);
            axes.add(0f);
            axes.add(0f);
            axes.add(1f);
            axes.add((float) x1 - MINOR_TICK_SIZE * (float) bounds.x);
            axes.add((float) y1 - MINOR_TICK_SIZE * (float) bounds.y);
            axes.add(tickZ);
            axes.add(0f);
            axes.add(0f);
            axes.add(1f);
            tickZ += (scaleZ / 10);
        }
        
        // create the array that we will copy into the VBO
        axesVertices = new float[axes.size()];
        for (int i = 0; i < axes.size(); i++) {
            axesVertices[i] = axes.get(i);
        }
        
        // create new VBO
        int[] vbo2 = new int[1];
        gl.glGenBuffers(1, vbo2, 0);
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo2[0]);
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, (long) axesVertices.length * Float.BYTES, FloatBuffer.wrap(axesVertices), GL3.GL_STATIC_DRAW);
        
        // create new VAO
        vao2 = new int[1];
        gl.glGenVertexArrays(1, vao2, 0);
        gl.glBindVertexArray(vao2[0]);
        
        // position
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 6 * Float.BYTES, 0);
        
        // color
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL3.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        
        gl.glBindVertexArray(0);
    }
   
    // delete things so we can close without causing a memory leak
    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
        GL3 gl = glAutoDrawable.getGL().getGL3();
        gl.glDeleteVertexArrays(1, vao1, 0);
        gl.glDeleteVertexArrays(1, vao2, 0);
        gl.glDeleteProgram(programId1);
        gl.glDeleteProgram(programId2);
    }
    
    // draw the scene
    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL3 gl = glAutoDrawable.getGL().getGL3();
        
        // clear the screen
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        
        // drawing the function
        // we are using the vertex array object associated with the function vertices
        // and the shader with it
        gl.glBindVertexArray(vao1[0]);
        gl.glUseProgram(programId1);
        // we need to pass the model view projection matrix to the shader, as well as the scale vector, for the shaders to use
        gl.glUniformMatrix4fv(gl.glGetUniformLocation(programId1, "mvp"), 1, false, FloatBuffer.wrap(mvpMatrix.getMatrix()));
        gl.glUniform3fv(gl.glGetUniformLocation(programId1, "scaleMajor"), 1, FloatBuffer.wrap(new float[]{scaleX, scaleY, scaleZ}));
        // draw the triangles
        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, vertices.length / 6);
        
        // drawing the axes
        // we are using the vertex array object associated with the axes vertices
        // and the shader with it
        gl.glBindVertexArray(vao2[0]);
        gl.glUseProgram(programId2);
        // still needs the model view projection matrix to place them in the right spot
        gl.glUniformMatrix4fv(gl.glGetUniformLocation(programId2, "mvp"), 1, false, FloatBuffer.wrap(mvpMatrix.getMatrix()));
        // renders them as lines instead of triangles
        gl.glDrawArrays(GL3.GL_LINES, 0, axesVertices.length / 6);
    }
    
    // called when the window is resized
    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
        GL3 gl = glAutoDrawable.getGL().getGL3();
        gl.glViewport(0, 0, i2, i3);
        float normalizedWidth = (float) i2 / (float) i3;
        float normalizedHeight = 1.0f;
        normalizedWidth *= cameraZoom;
        normalizedHeight *= cameraZoom;
        
        // recalculate the model view projection matrix
        Matrix4 modelMatrix = new Matrix4(); // translate and rescale model down to -1 to 1
        modelMatrix.scale((float) (2f / bounds.x), (float) (2f / bounds.y), (float) (2f / bounds.z));
        modelMatrix.translate((float) -center.x, (float) -center.y, (float) -center.z);
        
        Matrix4 viewMatrix = new Matrix4(); // camera orientation
        viewMatrix.rotate(cameraPitch, 1, 0, 0);
        viewMatrix.rotate(cameraYaw, 0, 1, 0);
        
        Matrix4 projectionMatrix = new Matrix4(); // orthographic projection, take window size into account
        projectionMatrix.makeOrtho(-normalizedWidth / 2, normalizedWidth / 2, -normalizedHeight / 2, normalizedHeight / 2, -2, 2);
        
        mvpMatrix = new Matrix4();
        mvpMatrix.multMatrix(projectionMatrix);
        mvpMatrix.multMatrix(viewMatrix);
        mvpMatrix.multMatrix(modelMatrix);
        
        display(glAutoDrawable);
    }
    
    public void startAnimation() {
        animator.start();
    }
}
