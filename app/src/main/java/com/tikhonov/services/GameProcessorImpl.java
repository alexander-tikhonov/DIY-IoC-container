package com.tikhonov.services;

import com.tikhonov.models.Equation;
import com.tikhonov.models.GameResult;
import com.tikhonov.models.Player;

import java.util.List;

public class GameProcessorImpl implements GameProcessor {

    private static final String MSG_HEADER = "Проверка знаний таблицы умножения";
    private static final String MSG_INPUT_BASE = "Введите цифру от 1 до 10";
    private static final String MSG_RIGHT_ANSWER = "Верно\n";
    private static final String MSG_WRONG_ANSWER = "Не верно\n";

    private final IOService ioService;
    private final PlayerService playerService;
    private final EquationPreparer equationPreparer;

    public GameProcessorImpl(IOService ioService,
                             PlayerService playerService,
                             EquationPreparer equationPreparer) {
        this.ioService = ioService;
        this.playerService = playerService;
        this.equationPreparer = equationPreparer;
    }

    @Override
    public void startGame() {
        ioService.out(MSG_HEADER);
        ioService.out("---------------------------------------");
        Player player = playerService.getPlayer();
        GameResult gameResult = new GameResult(player);

        int base = ioService.readInt(MSG_INPUT_BASE);
        List<Equation> equations = equationPreparer.prepareEquationsFor(base);
        equations.forEach(e -> {
            boolean isRight = ioService.readInt(e.toString()) == e.getResult();
            gameResult.incrementRightAnswers(isRight);
            ioService.out(isRight ? MSG_RIGHT_ANSWER : MSG_WRONG_ANSWER);
        });

        ioService.out(gameResult.toString());
    }
}
