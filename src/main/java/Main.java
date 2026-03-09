import javax.swing.JFrame;
import renderer.SimulationPanel;
import core.Simulation;
import core.Vector2D;
import core.Body;

class Main{
    static void main(String[] args){
        Simulation sim = new Simulation();
        SimulationPanel panel = new SimulationPanel(sim, 5e8);

        JFrame frame = new JFrame("N-Body");
        frame.setSize(1280, 800);
        frame.add(panel);

        Vector2D posEarth = new Vector2D(1.5e11, 0);
        Vector2D veloEarth = new Vector2D(0, 29000);

        Vector2D posSun = new Vector2D(0, 0);
        Vector2D veloSun = new Vector2D(0, 0);

        Body earth = new Body(posEarth, veloEarth, 5.972e24, 8);
        Body sun = new Body(posSun, veloSun, 1.989e30, 20);

        sim.objects.add(earth);
        sim.objects.add(sun);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}