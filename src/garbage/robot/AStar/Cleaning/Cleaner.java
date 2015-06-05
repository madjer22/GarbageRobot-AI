package garbage.robot.AStar.Cleaning;

import garbage.robot.Move;
import garbage.robot.State;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Vector;

public class Cleaner {

	private static int robotX;
	private static int robotY;
	private static char robotDirection;
	private static LinkedList<State> openedStates;
	private static LinkedList<State> closedStates;
	private static StainGrid grid;
	private static DirtyField purpose;
	private static LinkedList<Move> moves;
	private static Stack<Move> tempStack;

	private final static char N = 'N';
	private final static char W = 'W';
	private final static char E = 'E';
	private final static char S = 'S';

	private Cleaner() {

	}

	public static LinkedList<Move> clean(StainGrid parGrid) {
		grid = parGrid;
		robotX = grid.getSIZE_X() / 2;
		robotY = grid.getSIZE_Y() / 2;
		robotDirection = N;
		openedStates = new LinkedList<State>();
		closedStates = new LinkedList<State>();
		moves = new LinkedList<Move>();

		int uncleaned = grid.getDirtyFields();

		while (uncleaned > 0) {
			tempStack = new Stack<Move>();
			State tempState = new State(robotX, robotY, robotDirection, null);
			tempState.setG(0);
			tempState.setH(0);
			tempState.setF();
			purpose = findPurpose();
			//uncleaned--;

			openedStates.push(tempState);
			boolean finish = false;
			while (!openedStates.isEmpty() && !finish) {
				State currentState = openedStates.poll();
				closedStates.push(currentState);
				// znalezlismy
				if (currentState.getX() == purpose.getX()
						&& currentState.getY() == purpose.getY()) {
					uncleaned--;
					grid.cleanField(currentState.getX(), currentState.getY());
					robotX = currentState.getX();
					robotY = currentState.getY();
					robotDirection = currentState.getDirection();
					addMovesToStack(currentState.getParent(), currentState);
					stackToQueue();
					finish = true;
				} else {
					ArrayList<State> successors = makeSuccessors(currentState);
					for (State state : successors) {
						openedStates.add(state);
					}
					if(openedStates.size()>0){
					quickSort(openedStates, 0, openedStates.size() - 1);
					removeDuplicates(openedStates);
					}
				}
			}
		}
		return moves;
	}

	private static void stackToQueue() {
		while (!tempStack.empty()){
			moves.add(tempStack.pop());
		}
	}

	private static void addMovesToStack(State penultState, State lastState) {
		if (penultState == null)
			return;
		if (penultState.getParent() == null){
			tempStack.push(generateMove(penultState, lastState));
			return;
		}
		tempStack.push(generateMove(penultState, lastState));
		addMovesToStack(penultState.getParent(), penultState);
	}
	
	

	private static Move generateMove(State firstState, State secondState) {
		if(firstState.getDirection() == secondState.getDirection()){
			return Move.GO;
		}
		else{
			if (firstState.getDirection() == N){
				if (secondState.getDirection() == W){
					return Move.LEFT;
				}
				else{
					return Move.RIGHT;
				}
			}
			if (firstState.getDirection() == S){
				if (secondState.getDirection() == E){
					return Move.LEFT;
				}
				else{
					return Move.RIGHT;
				}
			}
			if (firstState.getDirection() == W){
				if (secondState.getDirection() == S){
					return Move.LEFT;
				}
				else{
					return Move.RIGHT;
				}
			}
			if (firstState.getDirection() == E){
				if (secondState.getDirection() == N){
					return Move.LEFT;
				}
				else{
					return Move.RIGHT;
				}
			}
		}
		return null;
	}

	public static void quickSort(LinkedList<State> openedStates, int start,
			int finish) {

		int i, j, v;
		State temp;

		i = start;
		j = finish;
		v = openedStates.get((start + finish) / 2).getF();
		do {
			while (openedStates.get(i).getF() < v)
				i++;
			while (v < openedStates.get(j).getF())
				j--;
			if (i <= j) {
				temp = new State(openedStates.get(i));
				openedStates.set(i, new State(openedStates.get(j)));
				openedStates.set(j, temp);
				i++;
				j--;
			}
		} while (i <= j);
		if (start < j)
			quickSort(openedStates, start, j);
		if (i < finish)
			quickSort(openedStates, i, finish);
	}

	public static void removeDuplicates(LinkedList<State> openedStates) {
		int stateIndex = 0;
		int i = 1;
		while (i <= openedStates.size() - 1) {
			if (openedStates.get(stateIndex).equate(openedStates.get(i))) {
				if (openedStates.get(stateIndex).getG() <= openedStates.get(i)
						.getG()) {
					openedStates.remove(i);
				} else {
					openedStates.remove(stateIndex);
				}
			} else {
				stateIndex++;
				i++;
			}
		}
	}

	private static ArrayList<State> makeSuccessors(State currentState) {
		ArrayList<State> result = new ArrayList<State>();

		switch (currentState.getDirection()) {
		case N: {
			// lewo
			if (!isClosed(currentState.getX(), currentState.getY(), W)) {
				State state = new State(currentState.getX(),
						currentState.getY(), W, currentState);
				state.setG(currentState.getG() + 3);
				state.setH(purpose);
				state.setF();
				result.add(state);
			}
			// prawo
			if (!isClosed(currentState.getX(), currentState.getY(), E)) {
				State state = new State(currentState.getX(),
						currentState.getY(), E, currentState);
				state.setG(currentState.getG() + 3);
				state.setH(purpose);
				state.setF();
				result.add(state);
			}
			// prosto
			if (!isClosed(currentState.getX(), currentState.getY() - 1, N)) {
				State state = new State(currentState.getX(),
						currentState.getY() - 1, N, currentState);
				state.setG(currentState.getG() + 5);
				state.setH(purpose);
				state.setF();
				result.add(state);
			}
			break;
		}
		case W: {
			// lewo
			if (!isClosed(currentState.getX(), currentState.getY(), S)) {
				State state = new State(currentState.getX(),
						currentState.getY(), S, currentState);
				state.setG(currentState.getG() + 3);
				state.setH(purpose);
				state.setF();
				result.add(state);
			}
			// prawo
			if (!isClosed(currentState.getX(), currentState.getY(), N)) {
				State state = new State(currentState.getX(),
						currentState.getY(), N, currentState);
				state.setG(currentState.getG() + 3);
				state.setH(purpose);
				state.setF();
				result.add(state);
			}
			// prosto
			if (!isClosed(currentState.getX() - 1, currentState.getY(), W)) {
				State state = new State(currentState.getX() - 1,
						currentState.getY(), W, currentState);
				state.setG(currentState.getG() + 5);
				state.setH(purpose);
				state.setF();
				result.add(state);
			}
			break;
		}
		case S: {
			// lewo
			if (!isClosed(currentState.getX(), currentState.getY(), E)) {
				State state = new State(currentState.getX(),
						currentState.getY(), E, currentState);
				state.setG(currentState.getG() + 3);
				state.setH(purpose);
				state.setF();
				result.add(state);
			}
			// prawo
			if (!isClosed(currentState.getX(), currentState.getY(), W)) {
				State state = new State(currentState.getX(),
						currentState.getY(), W, currentState);
				state.setG(currentState.getG() + 3);
				state.setH(purpose);
				state.setF();
				result.add(state);
			}
			// prosto
			if (!isClosed(currentState.getX(), currentState.getY() + 1, S)) {
				State state = new State(currentState.getX(),
						currentState.getY() + 1, S, currentState);
				state.setG(currentState.getG() + 5);
				state.setH(purpose);
				state.setF();
				result.add(state);
			}
			break;
		}
		case E: {
			// lewo
			if (!isClosed(currentState.getX(), currentState.getY(), N)) {
				State state = new State(currentState.getX(),
						currentState.getY(), N, currentState);
				state.setG(currentState.getG() + 3);
				state.setH(purpose);
				state.setF();
				result.add(state);
			}
			// prawo
			if (!isClosed(currentState.getX(), currentState.getY(), S)) {
				State state = new State(currentState.getX(),
						currentState.getY(), S, currentState);
				state.setG(currentState.getG() + 3);
				state.setH(purpose);
				state.setF();
				result.add(state);
			}
			// prosto
			if (!isClosed(currentState.getX() + 1, currentState.getY(), E)) {
				State state = new State(currentState.getX() + 1,
						currentState.getY(), E, currentState);
				state.setG(currentState.getG() + 5);
				state.setH(purpose);
				state.setF();
				result.add(state);
			}
			break;
		}
		}
		return result;
	}

	private static boolean isClosed(int x, int y, char direction) {
		if(x<0 || y<0 || x>=grid.getSIZE_X() || y>=grid.getSIZE_Y())
			return true;
		if (grid.getField(x, y) == '2')
			return true;
		for (State state : closedStates) {
			if (state.getX() == x && state.getY() == y
					&& state.getDirection() == direction) {
				return true;
			}
		}
		return false;
	}

	private static int calculateDistance(int xA, int yA, int xB, int yB) {
		return Math.abs(xA - xB) + Math.abs(yA - yB);
	}

	private static DirtyField findPurpose() {
		Vector<DirtyField> dirtyFields = new Vector<DirtyField>();
		DirtyField tempField = new DirtyField();
		boolean isStainFound = false;
		int dist = 1;
		if (grid.getField(robotX, robotY) == '1') {
			tempField = new DirtyField();
			tempField.setX(robotX);
			tempField.setY(robotY);
			tempField.setH(0);
			return tempField;
		}
		while (!isStainFound) {
			for (int i = (robotX - dist >= 0 ? robotX - dist : 0); i <= (robotX
					+ dist < grid.getSIZE_X() - 1 ? robotX + dist : grid
					.getSIZE_X() - 1); i++) {
				int verge = (robotY - dist) >= 0 ? (robotY - dist) : 0;
				try {
					if (grid.getField(i, verge) == '1') {
						tempField = new DirtyField();
						tempField.setX(i);
						tempField.setY(verge);
						tempField.setH(calculateDistance(robotX, robotY, i,
								verge));
						dirtyFields.add(tempField);
						isStainFound = true;
					}
				} catch (IndexOutOfBoundsException e) {
					System.err.println("blad gora x:" + i + " y: " + verge);
				}
			}
			for (int i = (robotY - dist >= 0 ? robotY - dist : 0); i <= (robotY
					+ dist < grid.getSIZE_Y() - 1 ? robotY + dist : grid
					.getSIZE_Y() - 1); i++) {
				int verge = (robotX + dist) < grid.getSIZE_X() ? (robotX + dist)
						: grid.getSIZE_X() - 1;
				try {
					if (grid.getField(verge, i) == '1') {
						tempField = new DirtyField();
						tempField.setX(verge);
						tempField.setY(i);
						tempField.setH(calculateDistance(robotX, robotY, verge,
								i));
						dirtyFields.add(tempField);
						isStainFound = true;
					}
				} catch (IndexOutOfBoundsException e) {
					System.err.println("blad prawo x:" + (verge) + " y: " + i);
				}
			}
			for (int i = (robotX - dist >= 0 ? robotX - dist : 0); i <= (robotX
					+ dist < grid.getSIZE_X() - 1 ? robotX + dist : grid
					.getSIZE_X() - 1); i++) {
				int verge = (robotY + dist) < grid.getSIZE_Y() ? (robotY + dist)
						: grid.getSIZE_Y() - 1;
				try {
					if (grid.getField(i, verge) == '1') {
						tempField = new DirtyField();
						tempField.setX(i);
						tempField.setY(verge);
						tempField.setH(calculateDistance(robotX, robotY, i,
								verge));
						dirtyFields.add(tempField);
						isStainFound = true;
					}
				} catch (IndexOutOfBoundsException e) {
					System.err.println("blad dol x:" + i + " y: " + verge);
				}
			}
			for (int i = (robotY - dist >= 0 ? robotY - dist : 0); i <= (robotY
					+ dist < grid.getSIZE_Y() - 1 ? robotY + dist : grid
					.getSIZE_Y() - 1); i++) {
				int verge = (robotX - dist) >= 0 ? (robotX - dist) : 0;
				try {

					if (grid.getField(verge, i) == '1') {
						tempField = new DirtyField();
						tempField.setX(verge);
						tempField.setY(i);
						tempField.setH(calculateDistance(robotX, robotY, verge,
								i));
						dirtyFields.add(tempField);
						isStainFound = true;
					}
				} catch (IndexOutOfBoundsException e) {
					System.err.println("blad lewo x:" + (verge) + " y: " + i);
				}

			}
			if (!isStainFound)
				dist++;
		}
		int minH = dirtyFields.get(0).getH();
		int minIndex = 0;

		for (int i = 1; i < dirtyFields.size(); i++) {
			if (dirtyFields.get(i).getH() < minH) {

				minH = dirtyFields.get(i).getH();
				minIndex = i;
			}
		}

		DirtyField result = dirtyFields.get(minIndex);
		dirtyFields.clear();
		return result;
	}
}