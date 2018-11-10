package mazemaker;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class MazeMaker {

    private static int width, height, columns, rows, delay;
    private static long seed;
    private static boolean showPath;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Random rnd = new Random();
                
                // Settings panel
                JSpinner jsRows = new JSpinner(new SpinnerNumberModel(36, 5, 300, 1));
                JSpinner jsColumns = new JSpinner(new SpinnerNumberModel(60, 5, 300, 1));
                JLabel jlRows = new JLabel("Rows", SwingConstants.RIGHT);
                JLabel jlColumns = new JLabel("Columns", SwingConstants.RIGHT);

                JLabel jlVres = new JLabel("Vertical res.", SwingConstants.RIGHT);
                JLabel jlHres = new JLabel("Horizontal res.", SwingConstants.RIGHT);
                JSpinner jsVres = new JSpinner(new SpinnerNumberModel(648, 360, 1080, 1));
                JSpinner jsHres = new JSpinner(new SpinnerNumberModel(1152, 640, 1920, 1));

                JLabel jlSeed = new JLabel("Seed", SwingConstants.RIGHT);
                JSpinner jsSeed = new JSpinner(new SpinnerNumberModel(0L, null, null, 1L));
                JLabel jlRandSeed = new JLabel("Random seed", SwingConstants.RIGHT);
                JButton jbRandSeed = new JButton("Generate");

                JLabel jlDelay = new JLabel("Delay (ms)", SwingConstants.RIGHT);
                JSpinner jsDelay = new JSpinner(new SpinnerNumberModel(20, 0, 1000, 5));
                JLabel jlShowPath = new JLabel("Show path", SwingConstants.RIGHT);
                JCheckBox jchShowPath = new JCheckBox("", true);

                JPanel sttgsPanel = new JPanel();
                sttgsPanel.setLayout(new GridLayout(0, 4, 10, 10));
                sttgsPanel.add(jlHres);
                sttgsPanel.add(jsHres);
                sttgsPanel.add(jlVres);
                sttgsPanel.add(jsVres);
                sttgsPanel.add(jlColumns);
                sttgsPanel.add(jsColumns);
                sttgsPanel.add(jlRows);
                sttgsPanel.add(jsRows);
                sttgsPanel.add(jlSeed);
                sttgsPanel.add(jsSeed);
                sttgsPanel.add(jlRandSeed);
                sttgsPanel.add(jbRandSeed);
                sttgsPanel.add(jlDelay);
                sttgsPanel.add(jsDelay);
                sttgsPanel.add(jlShowPath);
                sttgsPanel.add(jchShowPath);

                // Buttons panel
                ActionListener listener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        switch (evt.getActionCommand()) {
                            case "create":
                                width = (int) jsHres.getValue();
                                height = (int) jsVres.getValue();
                                columns = (int) jsColumns.getValue();
                                rows = (int) jsRows.getValue();
                                seed = (long) jsSeed.getValue();
                                delay = (int) jsDelay.getValue();
                                showPath = jchShowPath.isSelected();
                                showDialog();
                                break;
                            case "restart":
                                jsHres.setValue(1152);  jsVres.setValue(648);
                                jsColumns.setValue(60); jsRows.setValue(36);
                                jsSeed.setValue(0L);     jsDelay.setValue(20);
                                jchShowPath.setSelected(true);
                                break;
                            case "random":
                                jsSeed.setValue(rnd.nextLong());
                                break;
                            default:
                                break;
                        }
                    }
                };
                
                jbRandSeed.setActionCommand("random");
                jbRandSeed.addActionListener(listener);

                JButton jbCreate = new JButton("Create maze");
                jbCreate.setActionCommand("create");
                jbCreate.addActionListener(listener);

                JButton jbDefault = new JButton("Default values");
                jbDefault.setActionCommand("restart");
                jbDefault.addActionListener(listener);

                JPanel bttnPanel = new JPanel();
                bttnPanel.setLayout(new GridLayout());
                bttnPanel.add(jbDefault);
                bttnPanel.add(jbCreate);

                // Main panel 
                JPanel mPanel = new JPanel();
                mPanel.setLayout(new BorderLayout(15, 15));
                mPanel.add(bttnPanel, BorderLayout.SOUTH);
                mPanel.add(sttgsPanel, BorderLayout.CENTER);

                // Main Frame
                JFrame frame = new JFrame("Generador de laberinto");
                frame.setResizable(false);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(mPanel);
//                frame.pack();
                frame.setBounds(150,150,500,200);
                frame.setVisible(true);

            }

            private void showDialog() {

                FrameMaze dialog = new FrameMaze(width, height, columns, rows, delay, seed, showPath);

                class RunMaze extends SwingWorker<Void, Void> {

                    @Override
                    protected Void doInBackground() {
                        dialog.draw();
                        return null;
                    }

                    @Override
                    public void done() {
                    }
                }

                RunMaze rm = new RunMaze();
                rm.execute();

                dialog.setVisible(true);
            }
        });
    }
}
