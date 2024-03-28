package swervelib.encoders;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.SparkAbsoluteEncoder.Type;
import java.util.function.Supplier;

import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.Velocity;
import swervelib.motors.SwerveMotor;
import swervelib.telemetry.Alert;

/**
 * SparkMax absolute encoder, attached through the data port.
 */
public class SparkMaxEncoderSwerve extends SwerveAbsoluteEncoder
{

  /**
   * The {@link AbsoluteEncoder} representing the duty cycle encoder attached to the SparkMax.
   */
  public  AbsoluteEncoder encoder;
  /**
   * An {@link Alert} for if there is a failure configuring the encoder.
   */
  private Alert           failureConfiguring;
  /**
   * An {@link Alert} for if there is a failure configuring the encoder offset.
   */
  private Alert           offsetFailure;

  /**
   * Create the {@link SparkMaxEncoderSwerve} object as a duty cycle from the {@link CANSparkMax} motor.
   *
   * @param motor            Motor to create the encoder from.
   * @param conversionFactor The conversion factor to set if the output is not from 0 to 360.
   */
  public SparkMaxEncoderSwerve(SwerveMotor motor, int conversionFactor)
  {
    failureConfiguring = new Alert(
        "Encoders",
        "Failure configuring SparkMax Analog Encoder",
        Alert.AlertType.WARNING_TRACE);
    offsetFailure = new Alert(
        "Encoders",
        "Failure to set Absolute Encoder Offset",
        Alert.AlertType.WARNING_TRACE);
    if (motor.getMotor() instanceof CANSparkMax)
    {
      encoder = ((CANSparkMax) motor.getMotor()).getAbsoluteEncoder(Type.kDutyCycle);
      configureSparkMax(() -> encoder.setVelocityConversionFactor(conversionFactor));
      configureSparkMax(() -> encoder.setPositionConversionFactor(conversionFactor));
    } else
    {
      throw new RuntimeException("Motor given to instantiate SparkMaxEncoder is not a CANSparkMax");
    }
  }

  /**
   * Run the configuration until it succeeds or times out.
   *
   * @param config Lambda supplier returning the error state.
   */
  private void configureSparkMax(Supplier<REVLibError> config)
  {
    for (int i = 0; i < maximumRetries; i++)
    {
      if (config.get() == REVLibError.kOk)
      {
        return;
      }
    }
    failureConfiguring.set(true);
  }

  /**
   * Reset the encoder to factory defaults.
   */
  @Override
  public void factoryDefault()
  {
    // Do nothing
  }

  /**
   * Clear sticky faults on the encoder.
   */
  @Override
  public void clearStickyFaults()
  {
    // Do nothing
  }

  /**
   * Configure the absolute encoder to read from [0, 360) per second.
   *
   * @param inverted Whether the encoder is inverted.
   */
  @Override
  public void configure(boolean inverted)
  {
    encoder.setInverted(inverted);
  }

  /**
   * Get the absolute position of the encoder.
   *
   * @return Absolute position in a measure object
   */
  @Override
  public Measure<Angle> getAbsolutePosition()
  {
    return Units.Degrees.of(encoder.getPosition());
  }

  /**
   * Get the instantiated absolute encoder Object.
   *
   * @return Absolute encoder object.
   */
  @Override
  public Object getAbsoluteEncoder()
  {
    return encoder;
  }

  /**
   * Sets the Absolute Encoder Offset inside of the SparkMax's Memory.
   *
   * @param offset the offset the Absolute Encoder uses as the zero point.
   * @return if setting Absolute Encoder Offset was successful or not.
   */
  @Override
  public boolean setAbsoluteEncoderOffset(double offset)
  {
    REVLibError error = null;
    for (int i = 0; i < maximumRetries; i++)
    {
      error = encoder.setZeroOffset(offset);
      if (error == REVLibError.kOk)
      {
        return true;
      }
    }
    offsetFailure.setText("Failure to set Absolute Encoder Offset Error: " + error);
    offsetFailure.set(true);
    return false;
  }

  /**
   * Get the velocity in a velocity measure.
   *
   * @return velocity in a measure object.
   */
  @Override
  public Measure<Velocity<Angle>> getVelocity()
  {
    return Units.DegreesPerSecond.of(encoder.getVelocity());
  }
}
