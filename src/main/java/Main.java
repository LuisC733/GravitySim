import javax.swing.JFrame;
import renderer.*;
import core.*;
import physics.*;

class Main{
    static void main(String[] args){
        Simulation sim = new Simulation();
        SimulationPanel panel = new SimulationPanel(sim, 5e8);

        JFrame frame = new JFrame("N-Body");
        frame.setSize(1280, 800);
        frame.add(panel);

        Vector2D posSun = new Vector2D(0, 0);
        Vector2D veloSun = new Vector2D(0, 0);
        Body sun = new Body(posSun, veloSun, 1.989e30, 20);

        Vector2D posEarth = new Vector2D(1.5e11, 0);
        Vector2D veloEarth = new Vector2D(0, 29800);
        Body earth = new Body(posEarth, veloEarth, 5.97e24, 5);

        sim.objects.add(sun);
        sim.objects.add(earth);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}