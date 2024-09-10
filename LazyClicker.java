import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;
import java.awt.Robot;
import java.awt.MouseInfo;
import java.awt.Point;

public class LazyClicker extends JFrame {

    private static final long serialVersionUID = 1L;
    private Point clickPoint;
    private ScheduledExecutorService executor;
    private Robot robot;
    private boolean isRunning = false;

    public LazyClicker() {
        setTitle("Lazy Clicker");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        ((JPanel) getContentPane()).setAlignmentX(Component.CENTER_ALIGNMENT);
        setAlwaysOnTop(true);

        JButton captureMouseBtn = new JButton("Capturar Mouse (Pressione END)");
        captureMouseBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        captureMouseBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Mova o mouse até o local desejado e pressione END.");
            }
        });

        JTextField timerField = new JTextField("0.5", 5);
        timerField.setMaximumSize(new Dimension(60, 25));
        timerField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel timerLabel = new JLabel("Timer entre cliques (em segundos):", SwingConstants.CENTER);
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startAutoClickerBtn = new JButton("Iniciar Autoclick");
        startAutoClickerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startAutoClickerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                double interval = Double.parseDouble(timerField.getText());
                startAutoClicker(interval);
            }
        });

        JButton stopAutoClickerBtn = new JButton("Parar Autoclick");
        stopAutoClickerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        stopAutoClickerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopAutoClicker();
            }
        });

        JButton instructionsBtn = new JButton("Instruções");
        instructionsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Hotkey:\n- Capturar Mouse: Pressione END");
            }
        });

        JLabel creditLabel = new JLabel("By Braga", SwingConstants.CENTER);
        creditLabel.setForeground(Color.WHITE);
        creditLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createRigidArea(new Dimension(0, 10)));
        add(captureMouseBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(timerLabel);
        add(Box.createRigidArea(new Dimension(0, 5)));  // Adiciona padding ao redor do label do timer
        add(timerField);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(startAutoClickerBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(stopAutoClickerBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(instructionsBtn);
        add(Box.createRigidArea(new Dimension(0, 5)));  // Adiciona padding ao redor do crédito
        add(creditLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_END) {
                    captureMousePosition();
                }
                return false;
            }
        });

        try {
            robot = new Robot();
        } catch (AWTException e1) {
            e1.printStackTrace();
        }

        getContentPane().setBackground(Color.DARK_GRAY);
        captureMouseBtn.setBackground(Color.GRAY);
        captureMouseBtn.setForeground(Color.WHITE);
        startAutoClickerBtn.setBackground(Color.GRAY);
        startAutoClickerBtn.setForeground(Color.WHITE);
        stopAutoClickerBtn.setBackground(Color.GRAY);
        stopAutoClickerBtn.setForeground(Color.WHITE);
        instructionsBtn.setBackground(Color.GRAY);
        instructionsBtn.setForeground(Color.WHITE);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void captureMousePosition() {
        clickPoint = MouseInfo.getPointerInfo().getLocation();
        JOptionPane.showMessageDialog(null, "Posição do mouse capturada: " + clickPoint);
    }

    private void startAutoClicker(double interval) {
        if (clickPoint == null) {
            JOptionPane.showMessageDialog(null, "Por favor, capture a posição do mouse primeiro (END).");
            return;
        }
        if (!isRunning) {
            isRunning = true;
            executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    robot.mouseMove(clickPoint.x, clickPoint.y);
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                }
            }, 0, (long) (interval * 1000), TimeUnit.MILLISECONDS);
        }
    }

    private void stopAutoClicker() {
        if (isRunning && executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                Thread.sleep(100);  // Pequeno atraso para garantir que o clique seja registrado antes de parar
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isRunning = false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LazyClicker();
            }
        });
    }
}
