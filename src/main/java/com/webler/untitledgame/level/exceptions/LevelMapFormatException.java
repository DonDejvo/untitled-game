package com.webler.untitledgame.level.exceptions;

public class LevelMapFormatException extends Exception {
    public LevelMapFormatException(String fileName) {
        super("Level map file format error: " + fileName);
    }
}
