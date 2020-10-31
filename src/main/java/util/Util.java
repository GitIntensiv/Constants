package util;

import base.H2;
import dictionary.Message;
import model.Constant;
import model.Participant;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Util {
    public static String printConstants(List<Constant> constants) {
        if (constants.size() == 0) {
            return Message.NOT_CREATE_YET.getText();
        }
        StringBuilder builder = new StringBuilder();
        for (Constant c : constants) {
            builder.append(c).append("\n\n");
        }
        return builder.toString();
    }

    public static String createTextWithWinners(Constant constant) {
        List<Participant> participants = H2.getInstance().getAllParticipantsByConstantId(constant.getId());
        int countWinners = constant.getCountWinners();
        StringBuilder text = new StringBuilder(constant.getText())
                .append("\n\\*\\*\\*\\*\\*\n")
                .append("Победители: ");
        Set<Integer> winners = new HashSet<>();
        if (participants.size() <= countWinners) {
            for (int i = 0; i < participants.size(); i++) {
                winners.add(i);
            }
        } else {
            while (winners.size() < countWinners) {
                winners.add(1 + (int) (Math.random() * (participants.size() - 1)));
            }
        }
        for (Integer l : winners) {
            Participant p = participants.get(l);
            text.append("\n").append("[").append(p.getName()).append("](").append(p.getUsername()).append(") ");
        }
        return text.toString();
    }
}
