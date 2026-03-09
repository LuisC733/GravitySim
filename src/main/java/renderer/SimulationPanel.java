package renderer;

import core.Simulation;
import core.Vector2D;
import javax.swing.JPanel;
import java.awt.Graphics;
import javax.swing.Timer;
import java.awt.event.*;
import java.awt.Color;

public class SimulationPanel extends JPanel{
    Simulation sim;
    double scale;
    public SimulationPanel(Simulation sim, double scale){
        this.sim = sim;
        this.scale = scale;

        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sim.updatePos();
                repaint();
            }
        };
        Timer timer = new Timer(16, action);
        timer.start();
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.RED);
        for(int i = 0; i < sim.objects.size(); i++){
            double radius = sim.objects.get(i).getRadius();
            Vector2D pos = sim.objects.get(i).getPos();
            g.fillOval((int)(pos.x / scale) + 640, (int)(pos.y / scale) + 400, (int)radius, (int)radius);
        }
    }
}