import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SimplePainter extends JFrame {

    private Color currentColor = Color.BLACK;
    private int brushSize = 5;
    private String brushShape = "Pen";
    private BufferedImage canvasImage;
    private Graphics2D g2d;
    private JPanel canvas;
    private int startX, startY, endX, endY;
    private boolean drawing;
    private int lastX, lastY;

    public SimplePainter() {
        setTitle("Simple Painter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // 禁用視窗縮放

        canvasImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        g2d = canvasImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
        g2d.setColor(currentColor);

        JPanel toolbar = new JPanel();
        JButton colorButton = new JButton("Choose Color");
        colorButton.addActionListener(e -> chooseColor());
        toolbar.add(colorButton);

        JSpinner brushSizeSpinner = new JSpinner(new SpinnerNumberModel(brushSize, 1, 50, 1));
        brushSizeSpinner.addChangeListener(e -> brushSize = (int) brushSizeSpinner.getValue());
        toolbar.add(new JLabel("Brush Size:"));
        toolbar.add(brushSizeSpinner);

        String[] shapes = {"Pen", "Circle", "Square", "Line"};
        JComboBox<String> shapeComboBox = new JComboBox<>(shapes);
        shapeComboBox.addActionListener(e -> brushShape = (String) shapeComboBox.getSelectedItem());
        toolbar.add(new JLabel("Brush Shape:"));
        toolbar.add(shapeComboBox);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearCanvas());
        toolbar.add(clearButton);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveCanvas());
        toolbar.add(saveButton);

        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(e -> loadCanvas());
        toolbar.add(loadButton);

        getContentPane().add(toolbar, BorderLayout.NORTH);

        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(canvasImage, 0, 0, null);
                if (drawing) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(currentColor);
                    g2.setStroke(new BasicStroke(brushSize));
                    if (brushShape.equals("Square")) {
                        int x = Math.min(startX, endX);
                        int y = Math.min(startY, endY);
                        int width = Math.abs(startX - endX);
                        int height = Math.abs(startY - endY);
                        g2.drawRect(x, y, width, height);
                    } else if (brushShape.equals("Circle")) {
                        int x = Math.min(startX, endX);
                        int y = Math.min(startY, endY);
                        int width = Math.abs(startX - endX);
                        int height = Math.abs(startY - endY);
                        g2.drawOval(x, y, width, height);
                    } else if (brushShape.equals("Line")) {
                        g2.drawLine(startX, startY, endX, endY);
                    }
                }
            }
        };

        canvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (brushShape.equals("Pen")) {
                    drawPen(lastX, lastY, e.getX(), e.getY());
                    lastX = e.getX();
                    lastY = e.getY();
                } else {
                    endX = e.getX();
                    endY = e.getY();
                    drawing = true;
                    canvas.repaint();
                }
            }
        });

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
                lastX = startX;
                lastY = startY;
                if (!brushShape.equals("Pen")) {
                    endX = startX;
                    endY = startY;
                    drawing = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (brushShape.equals("Square")) {
                    endX = e.getX();
                    endY = e.getY();
                    drawRectangle(startX, startY, endX, endY);
                } else if (brushShape.equals("Circle")) {
                    endX = e.getX();
                    endY = e.getY();
                    drawCircle(startX, startY, endX, endY);
                } else if (brushShape.equals("Line")) {
                    endX = e.getX();
                    endY = e.getY();
                    drawLine(startX, startY, endX, endY);
                }
                drawing = false;
                canvas.repaint();
            }
        });

        getContentPane().add(canvas, BorderLayout.CENTER);
    }

    private void chooseColor() {
        Color chosenColor = JColorChooser.showDialog(this, "Choose Brush Color", currentColor);
        if (chosenColor != null) {
            currentColor = chosenColor;
            g2d.setColor(currentColor);
        }
    }

    private void clearCanvas() {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
        g2d.setColor(currentColor);
        canvas.repaint();
    }

    private void drawPen(int x1, int y1, int x2, int y2) {
        g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(x1, y1, x2, y2);
        canvas.repaint();
    }

    private void drawRectangle(int x1, int y1, int x2, int y2) {
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x1 - x2);
        int height = Math.abs(y1 - y2);
        g2d.setStroke(new BasicStroke(brushSize));
        g2d.drawRect(x, y, width, height);
    }

    private void drawCircle(int x1, int y1, int x2, int y2) {
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x1 - x2);
        int height = Math.abs(y1 - y2);
        g2d.setStroke(new BasicStroke(brushSize));
        g2d.drawOval(x, y, width, height);
    }

    private void drawLine(int x1, int y1, int x2, int y2) {
        g2d.setStroke(new BasicStroke(brushSize));
        g2d.drawLine(x1, y1, x2, y2);
    }

    private void saveCanvas() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                ImageIO.write(canvasImage, "png", fileToSave);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadCanvas() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Image");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            try {
                BufferedImage loadedImage = ImageIO.read(fileToOpen);
                g2d.drawImage(loadedImage, 0, 0, null);
                canvas.repaint();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimplePainter painter = new SimplePainter();
            painter.setVisible(true);
        });
    }
}
