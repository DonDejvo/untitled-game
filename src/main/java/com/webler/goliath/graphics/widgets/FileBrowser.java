package com.webler.goliath.graphics.widgets;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBrowser {
    private static final String NAME = "FileBrowser";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    private static FileBrowser instance = null;
    private Path currentPath;
    private String searchText;
    private String fileNameText;
    private Stack<Path> history;
    private int backCounter;
    private Path resultPath;
    private int currentIndex;
    private FileBrowserAction action;

    public static FileBrowser getInstance() {
        if (instance == null) {
            instance = new FileBrowser();
        }
        return instance;
    }

    public FileBrowser() {
        currentPath = Paths.get(System.getProperty("user.home"));
        searchText = currentPath.toAbsolutePath().toString();
        fileNameText = "";
        history = new Stack<>();
        backCounter = 0;
        history.push(currentPath);
        resultPath = null;
        currentIndex = 0;
        action = FileBrowserAction.NONE;
    }

    public static void open(FileBrowserAction type) {
        ImGui.openPopup(NAME);
        getInstance().action = type;
    }

    public static boolean getModal() {
        return getInstance().imgui();
    }

    public static String getResultPath() {
        return getInstance().resultPath == null ? null : getInstance().resultPath.toString();
    }

    public static FileBrowserAction getAction() {
        return getInstance().action;
    }

    private boolean imgui() {
        boolean result = false;
        List<Path> files = getFiles();

        resultPath = null;

        ImGuiIO io = ImGui.getIO();
        ImGui.setNextWindowSize(800, 600, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowPos(io.getDisplaySizeX() * 0.5f - 400, io.getDisplaySizeY() * 0.5f - 300, ImGuiCond.FirstUseEver);

        if(ImGui.beginPopupModal(NAME)) {
            if(ImGui.button("<")) {
                goBackHistory();
            }
            ImGui.sameLine();
            if(ImGui.button(">")) {
                goForwardHistory();
            }
            ImGui.sameLine();
            ImGui.pushID("SearchInput");
            ImString searchInputText = new ImString(searchText, 256);
            ImGui.pushItemWidth(ImGui.getContentRegionAvailX());
            if (ImGui.inputText("##SearchInput", searchInputText, ImGuiInputTextFlags.EnterReturnsTrue)) {
                searchText = searchInputText.get();
                openDirectory(Path.of(searchText));
            }
            ImGui.popItemWidth();
            ImGui.popID();

            ImGui.pushID("ListBox");
            if (ImGui.beginListBox("##ListBox",
                    ImGui.getContentRegionAvailX(),
                    ImGui.getContentRegionAvailY() - (action == FileBrowserAction.SAVE ? 2.5f : 1.5f) * ImGui.getTextLineHeightWithSpacing())) {
                ImGui.columns(4);
                ImGui.text("Name");
                ImGui.nextColumn();
                ImGui.text("Last modified time");
                ImGui.nextColumn();
                ImGui.text("Type");
                ImGui.nextColumn();
                ImGui.text("Size");
                ImGui.nextColumn();
                for(int i = 0; i < files.size(); i++) {
                    drawFileItem(files, i);
                }
                ImGui.columns(1);
                ImGui.endListBox();
            }
            ImGui.popID();

            if(action == FileBrowserAction.SAVE) {
                ImGui.text("File Name: ");
                ImGui.sameLine();
                ImGui.pushID("FileNameInput");
                ImString fileNameInputText = new ImString(fileNameText, 256);
                ImGui.pushItemWidth(ImGui.getContentRegionAvailX());
                if (ImGui.inputText("##FileNameInput", fileNameInputText)) {
                    fileNameText = fileNameInputText.get();
                }
                ImGui.popItemWidth();
                ImGui.popID();
            }

            if(ImGui.button("Cancel")) {
                ImGui.closeCurrentPopup();
                result = true;
            }
            ImGui.sameLine();
            if(action == FileBrowserAction.OPEN) {
                if(ImGui.button("Open")) {
                    ImGui.closeCurrentPopup();
                    if(currentIndex < files.size()) {
                        Path path = files.get(currentIndex).toAbsolutePath();
                        result = setPathToOpenFile(path);
                    }
                }
            } else if(action == FileBrowserAction.SAVE) {
                if(ImGui.button("Save")) {
                    ImGui.closeCurrentPopup();
                    result = setPathToSaveFile();
                }
            }

            ImGui.endPopup();
        }

        return result;
    }

    private void drawFileItem(List<Path> files, int idx) {
        String fileName = files.get(idx).getFileName().toString();
        Path filePath = Path.of(currentPath.toString(), fileName);
        boolean selected = currentIndex == idx;
        if(ImGui.selectable(fileName, selected)) {
            currentIndex = idx;
            openDirectory(filePath);
        }
        if(selected) {
            ImGui.setItemDefaultFocus();
        }
        ImGui.nextColumn();

        String lastModifiedTime = "";
        String mimeType = "";
        String size = "";
        try {
            BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);

            lastModifiedTime = formatDateTime(attr.lastModifiedTime());

            if(attr.isRegularFile()) {
                size = String.format("%.0f kB", Math.ceil(attr.size() / 1000.0));

                try {
                    String probeContentType = Files.probeContentType(filePath);
                    if(probeContentType != null) {
                        mimeType = probeContentType;
                    }
                } catch (IOException ignored) {
                }
            } else {
                mimeType = "Folder";
            }

        } catch (IOException ignored) {
        }

        ImGui.text(lastModifiedTime);
        ImGui.nextColumn();
        ImGui.text(mimeType);
        ImGui.nextColumn();
        ImGui.text(size);
        ImGui.nextColumn();
    }

    private boolean setPathToSaveFile() {
        if(!fileNameText.isEmpty()) {
            resultPath = Path.of(instance.currentPath.toString(), instance.fileNameText);
            return true;
        }
        return false;
    }

    private boolean setPathToOpenFile(Path path) {
        if(!Files.isDirectory(path)) {
            resultPath = Path.of(path.toString());
            return true;
        }
        return false;
    }

    private List<Path> getFiles() {
        try(Stream<Path> files = Files.list(currentPath)) {
            return files.sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void goBackHistory() {
        if(backCounter < history.size() - 1) {
            ++backCounter;
            Path path = history.get(history.size() - backCounter - 1);
            setCurrentPath(path);
        }
    }

    private void goForwardHistory() {
        if(backCounter > 0) {
            Path path = history.get(history.size() - backCounter);
            setCurrentPath(path);
            --backCounter;
        }
    }

    private void setCurrentPath(Path path) {
        if(Files.isDirectory(path)) {
            currentPath = path;
            currentIndex = 0;
        }
        searchText = currentPath.toAbsolutePath().toString();
    }

    private void openDirectory(Path path) {
        if(Files.isDirectory(path)) {
            while(backCounter > 0) {
                history.pop();
                --backCounter;
            }
            history.push(path);
            setCurrentPath(path);

        }
    }

    private static String formatDateTime(FileTime fileTime) {

        LocalDateTime localDateTime = fileTime
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return localDateTime.format(DATE_FORMATTER);
    }
}
