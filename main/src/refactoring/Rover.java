package refactoring;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

public class Rover {
	private Heading heading;
	private Position position;
	private static Map<Position, Obstacle> obstacles;

	public Rover(String facing, int x, int y) {
		this(Heading.of(facing),new Position(x,y));
	}

	public Rover(Heading heading, int x, int y) { this(heading, new Position(x,y)); }

	public Rover(Heading heading, Position position) {
		this.heading = heading;
		this.position = position;
		obstacles = new HashMap<>();
	}

	public void addObstacle(Obstacle obstacle) { obstacles.put(obstacle.getPosition(),obstacle); }

	public Heading heading() {
		return heading;
	}

	public Position position() {
		return this.position;
	}

	public void go(String instructions){ go(stream(instructions.split("")).map(Order::of)); }

	public void go(Order... orders){ go(stream(orders)); }

	private void go(Stream<Order> orders){ orders.filter(Objects::nonNull).forEach(this::execute); }

	private void execute (Order order){ actions.get(order).execute(); }

	public static class Position {
		private final int x;
		private final int y;

		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public Position forward (Heading heading) {
			if (thereIsObstacleBefore(heading)) return this;
			else return forwardPosition(heading);
		}

		public Position backward (Heading heading) {
			if (thereIsObstacleBehind(heading)) return this;
			else return backwardPosition(heading);
		}

		private boolean thereIsObstacleBefore(Heading heading) {
			return thereIsObstacle(forwardPosition(heading));
		}

		private boolean thereIsObstacleBehind(Heading heading) {
			return thereIsObstacle(backwardPosition(heading));
		}

		private boolean thereIsObstacle(Position position) { return Rover.obstacles.containsKey(position); }

		private Position forwardPosition(Heading heading) {
			return new Position(this.x + dx(heading), this.y + dy(heading));
		}

		private Position backwardPosition(Heading heading){ return new Position(this.x - dx(heading), this.y - dy(heading)); }

		private int dx(Heading heading) {
			if (heading == Heading.East) return 1;
			if (heading == Heading.West) return -1;
			return 0;
		}

		private int dy(Heading heading) {
			if (heading == Heading.North) return 1;
			if (heading == Heading.South) return -1;
			return 0;
		}

		@Override
		public String toString() { return "Position{" + "x=" + x + ", y=" + y + '}'; }

		@Override
		public boolean equals(Object object) {
			return isSameClass(object) && equals((Position) object);
		}

		private boolean isSameClass(Object object) { return object != null && object.getClass() == Position.class; }

		private boolean equals(Position position) { return position == this || (x == position.x && y == position.y); }

		@Override
		public int hashCode() { return Objects.hash(x, y); }
	}

	Map<Order,Action> actions = new HashMap<>();
	{
		actions.put(Order.Forward, ()->position = position.forward(heading));
		actions.put(Order.Backward, ()->position = position.backward(heading));
		actions.put(Order.Left, ()->heading = heading.turnLeft());
		actions.put(Order.Right, ()->heading = heading.turnRight());
	}

	public enum Order {
		Forward, Backward, Left, Right;

		public static Order of(String instruction) {
			if (instruction.equals("F")) return Forward;
			if (instruction.equals("B")) return Backward;
			if (instruction.equals("L")) return Left;
			if (instruction.equals("R")) return Right;
			return null;
		}
	}

	@FunctionalInterface
	public interface Action {
		void execute();
	}


	public enum Heading {
		North, East, South, West;

		public static Heading of(String label) {
			return of(label.charAt(0));
		}

		public static Heading of(char label) {
			if (label == 'N') return North;
			if (label == 'S') return South;
			if (label == 'W') return West;
			if (label == 'E') return East;
			return null;
		}

		public Heading turnRight() {
			return values()[add(+1)];
		}

		public Heading turnLeft() {
			return values()[add(-1)];
		}

		private int add(int offset) {
			return (this.ordinal() + offset + values().length) % values().length;
		}
	}
}