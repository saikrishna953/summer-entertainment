package org.acme;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {

    private String chosenTeam;
    private double betAmount = 50;
    private double creditAmount=0;

}
