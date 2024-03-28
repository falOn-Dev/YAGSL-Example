package swervelib.math;

import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.units.Mass;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Units;

/**
 * Object with significant mass that needs to be taken into account.
 */
public class Matter
{

  /**
   * Position in meters from robot center in 3d space.
   */
  public Translation3d position;
  /**
   * Mass in a measure object, of the object.
   */
  public Measure<Mass> mass;

  /**
   * Construct an object representing some significant matter on the robot.
   *
   * @param position Position of the matter in meters.
   * @param mass     Mass in a measure object.
   */
  public Matter(Translation3d position, Measure<Mass> mass)
  {
    this.mass = mass;
    this.position = position;
  }

  /**
   * Get the center mass of the object.
   *
   * @return center mass = position * mass
   */
  public Translation3d massMoment()
  {
    return position.times(mass.in(Units.Kilogram));
  }
}
