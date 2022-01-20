public class LinearSmoother extends Smoother {
    static final double LAMBDA = 0.5;
    @Override
    protected void update(double target) {
        if (current != target) {
            current = current + clamp(deltaTime() * (target - current));
        } 
    }
    
    private static double clamp(double num) {
        if (num > LAMBDA) return LAMBDA;
        else if (num < -LAMBDA) return -LAMBDA; 
        else return num;
    }
}
