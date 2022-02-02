package frc.robot.framework;

public final class Order implements Comparable<Order>
{
    // Note: earlier is smaller valued
    private int order;

    public static final Order 

        BEGIN           = new Order(Integer.MIN_VALUE),

        EARLY_INPUT     = new Order(-600_000),
        INPUT           = new Order(-500_000),
        LATE_INPUT      = new Order(-400_000),

        EARLY_EXECUTION = new Order(-300_000),
        EXECUTION       = new Order(0),
        LATE_EXECUTION  = new Order(300_000),

        EARLY_OUTPUT    = new Order(400_000),
        OUTPUT          = new Order(500_000),
        LATE_OUTPUT     = new Order(600_000),

        END             = new Order(Integer.MAX_VALUE)

    ;
    
    public static Order after(Order o)
    {
        var curPri = o.order;
        return new Order(curPri == Integer.MAX_VALUE ? curPri : curPri + 1);
    }
    public static Order before(Order o)
    {
        var curPri = o.order;
        return new Order(curPri == Integer.MIN_VALUE ? curPri : curPri - 1);
    }

    public Order(int priority)
    {
        this.order = priority;
    }

    @Override
    public int compareTo(Order o) 
    {
        return Integer.compare(this.order, o.order);
    }
}
