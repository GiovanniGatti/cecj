package cecj.app;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import games.player.EvolvedPlayer;
import games.player.LearningPlayer;
import games.scenario.TwoPlayerTDLScenario;

public class PopulationTDLImprover extends SelfPlayTDLImprover {

	private static final String P_OPPONENTS = "opponents";

	private int opponents;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter opponentsParam = base.push(P_OPPONENTS);
		opponents = state.parameters.getInt(opponentsParam, null);
	}

	@Override
	public void improve(EvolutionState state, Individual ind) {
		EvolvedPlayer player = playerPrototype.createEmptyCopy();
		player.readFromIndividual(ind);

		Individual[] inds = state.population.subpops[0].individuals;
		for (int o = 0; o < opponents; o++) {
			Individual ind2 = inds[state.random[0].nextInt(inds.length)];
			EvolvedPlayer opponent = playerPrototype.createEmptyCopy();
			opponent.readFromIndividual(ind2);

			if (player instanceof LearningPlayer && opponent instanceof LearningPlayer) {
				TwoPlayerTDLScenario scenario = new TwoPlayerTDLScenario(new LearningPlayer[] {
						(LearningPlayer) player, (LearningPlayer) opponent }, randomness,
						learningRate, 0);
				TwoPlayerTDLScenario scenario2 = new TwoPlayerTDLScenario(new LearningPlayer[] {
						(LearningPlayer) opponent, (LearningPlayer) player }, randomness,
						learningRate, 1);

				for (int r = 0; r < repeats; r++) {
					boardGame.reset();
					scenario.play(boardGame);
				}

				for (int r = 0; r < repeats; r++) {
					boardGame.reset();
					scenario2.play(boardGame);
				}
			} else {
				state.output.fatal("Can not improve players which are not LearningPlayer instances");
			}
		}
	}
}
