import java.util.Map;

public class EnumUtilities {
    public static final Map<String, Sensor> sensorNames = Map.of(
      "fuelLeft", Sensor.FuelLeft,
      "oppLR", Sensor.OppLR,
      "oppFB", Sensor.OppFB,
        "numBarrels", Sensor.NumBarrels,
        "barrelLR", Sensor.BarrelLR,
        "barrelFB", Sensor.BarrelFB,
        "wallDist", Sensor.WallDist
    );

    public static final Map<String, ArithmeticOperator> arithmeticOperatorNames = Map.of(
            "add", ArithmeticOperator.Add,
            "sub", ArithmeticOperator.Subtract,
            "mul", ArithmeticOperator.Multiply,
            "div", ArithmeticOperator.Divide
    );

    public static Sensor parseSensorNameOrNull(String sensorName) {
        return sensorNames.getOrDefault(sensorName, null);
    }

    public static ArithmeticOperator parseArithmeticOperatorOrNull(String arithmeticOperator) {
        return arithmeticOperatorNames.getOrDefault(arithmeticOperator, null);
    }
}
