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
    private final Stack<Path> history;
    private int backCounter;
    private Path resultPath;
    private int currentIndex;
    private FileBrowserAction action;

    /**
    * Returns the singleton instance of FileBrowser. This method is thread safe. Do not use it in production code.
    * 
    * 
    * @return the singleton instance of FileBrowser or null if none exists in the system ( such as when the user clicks on the file
    */
    public static FileBrowser getInstance() {
        // Returns the FileBrowser instance.
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

    /**
    * Opens the file browser. This is a shortcut for #openPopup ( String ). It will set the action to the given type and the dialog will be closed when the user clicks on the button
    * 
    * @param type - the type of file
    */
    public static void open(FileBrowserAction type) {
        ImGui.openPopup(NAME);
        getInstance().action = type;
    }

    /**
    * Returns true if imgui is modal. This is used to prevent user interfering with image selection and other things that might be in the middle of the dialog.
    * 
    * 
    * @return true if imgui is modal false otherwise ( no modal or not set yet ) Note : the return value is ignored
    */
    public static boolean getModal() {
        return getInstance().imgui();
    }

    /**
    * Returns the result path. Note that this will be null if #getResultPath () is null.
    * 
    * 
    * @return the result path or null if #getResultPath () is null or not set in the configuration file
    */
    public static String getResultPath() {
        return getInstance().resultPath == null ? null : getInstance().resultPath.toString();
    }

    /**
    * Returns the FileBrowserAction associated with this instance. If there is no action associated with this instance null is returned.
    * 
    * 
    * @return the FileBrowserAction associated with this instance or null if there is no action associated with this instance or
    */
    public static FileBrowserAction getAction() {
        return getInstance().action;
    }

    /**
    * Method to show ImGui. This method is called by #main (). If user clicks OK the method returns true else false.
    * 
    * 
    * @return true if user clicks OK false otherwise. In case of error the method returns false and details about the
    */
    private boolean imgui() {
        boolean result = false;
        List<Path> files = getFiles();

        resultPath = null;

        ImGuiIO io = ImGui.getIO();
        ImGui.setNextWindowSize(800, 600, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowPos(io.getDisplaySizeX() * 0.5f - 400, io.getDisplaySizeY() * 0.5f - 300, ImGuiCond.FirstUseEver);

        // This method is called by the user to open the popup modal.
        if(ImGui.beginPopupModal(NAME)) {
            // Go back history if the button is pressed.
            if(ImGui.button("<")) {
                goBackHistory();
            }
            ImGui.sameLine();
            // Go forward history if the button is pressed.
            if(ImGui.button(">")) {
                goForwardHistory();
            }
            ImGui.sameLine();
            ImGui.pushID("SearchInput");
            ImString searchInputText = new ImString(searchText, 256);
            ImGui.pushItemWidth(ImGui.getContentRegionAvailX());
            // Opens the search directory if it exists.
            if (ImGui.inputText("##SearchInput", searchInputText, ImGuiInputTextFlags.EnterReturnsTrue)) {
                searchText = searchInputText.get();
                openDirectory(Path.of(searchText));
            }
            ImGui.popItemWidth();
            ImGui.popID();

            ImGui.pushID("ListBox");
            // ListBox method for the list box
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
                // Draw all the files in the list
                for(int i = 0; i < files.size(); i++) {
                    drawFileItem(files, i);
                }
                ImGui.columns(1);
                ImGui.endListBox();
            }
            ImGui.popID();

            // Save the file name.
            if(action == FileBrowserAction.SAVE) {
                ImGui.text("File Name: ");
                ImGui.sameLine();
                ImGui.pushID("FileNameInput");
                ImString fileNameInputText = new ImString(fileNameText, 256);
                ImGui.pushItemWidth(ImGui.getContentRegionAvailX());
                // Set the fileNameInputText to the fileNameInputText.
                if (ImGui.inputText("##FileNameInput", fileNameInputText)) {
                    fileNameText = fileNameInputText.get();
                }
                ImGui.popItemWidth();
                ImGui.popID();
            }

            // Cancel the current popup.
            if(ImGui.button("Cancel")) {
                ImGui.closeCurrentPopup();
                result = true;
            }
            ImGui.sameLine();
            // Open or Save files.
            if(action == FileBrowserAction.OPEN) {
                // Open the current file.
                if(ImGui.button("Open")) {
                    ImGui.closeCurrentPopup();
                    // Set the current file to open file
                    if(currentIndex < files.size()) {
                        Path path = files.get(currentIndex).toAbsolutePath();
                        result = setPathToOpenFile(path);
                    }
                }
            // Save the file to save the file.
            } else if(action == FileBrowserAction.SAVE) {
                // Save the current popup and set the path to save file
                if(ImGui.button("Save")) {
                    ImGui.closeCurrentPopup();
                    result = setPathToSaveFile();
                }
            }

            ImGui.endPopup();
        }

        return result;
    }

    /**
    * Draws a file or directory. This method is called by #drawDirectory ( java. util. List ) when the user clicks on a file or directory in the file selector.
    * 
    * @param files - The list of files to be selected. This list is modified by this method
    * @param idx - The index of the file in the
    */
    private void drawFileItem(List<Path> files, int idx) {
        String fileName = files.get(idx).getFileName().toString();
        Path filePath = Path.of(currentPath.toString(), fileName);
        boolean selected = currentIndex == idx;
        // Opens the directory if selected.
        if(ImGui.selectable(fileName, selected)) {
            currentIndex = idx;
            openDirectory(filePath);
        }
        // Set the default focus to the selected item.
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

            // Returns the file type of the file.
            if(attr.isRegularFile()) {
                size = String.format("%.0f kB", Math.ceil(attr.size() / 1000.0));

                try {
                    String probeContentType = Files.probeContentType(filePath);
                    // Set the MIME type of the probe.
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

    /**
    * Sets the resultPath to the path where the save file is located. If there is no fileNameText it returns false
    * 
    * 
    * @return true if the path was
    */
    private boolean setPathToSaveFile() {
        // Returns true if fileNameText is empty.
        if(!fileNameText.isEmpty()) {
            resultPath = Path.of(instance.currentPath.toString(), instance.fileNameText);
            return true;
        }
        return false;
    }

    /**
    * Sets resultPath to the path that should be opened. This is a helper method for #open ( java. io. File ).
    * 
    * @param path - the path to check. If it is a directory it is ignored.
    * 
    * @return true if the path was set false otherwise ( and no changes were made ). In this case #resultPath is set
    */
    private boolean setPathToOpenFile(Path path) {
        // Returns true if the path is a directory.
        if(!Files.isDirectory(path)) {
            resultPath = Path.of(path.toString());
            return true;
        }
        return false;
    }

    /**
    * Returns a list of files in the current directory sorted lexicographically. This is useful for debugging and to ensure that files are in the correct order when they are added to the working directory.
    * 
    * 
    * @return the list of files in the current directory sorted lexicographically or an empty list if there is an
    */
    private List<Path> getFiles() {
        try(Stream<Path> files = Files.list(currentPath)) {
            return files.sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
    * Go back in history if there is one. This is used to handle navigation to the previous page of
    */
    private void goBackHistory() {
        // This method will update the current path to the last path in the history.
        if(backCounter < history.size() - 1) {
            ++backCounter;
            Path path = history.get(history.size() - backCounter - 1);
            setCurrentPath(path);
        }
    }

    /**
    * Goes forward in history if there is at least one path to go back. This is used when the user presses Enter
    */
    private void goForwardHistory() {
        // Move back to the current path.
        if(backCounter > 0) {
            Path path = history.get(history.size() - backCounter);
            setCurrentPath(path);
            --backCounter;
        }
    }

    /**
    * Sets the current path. If the path is a directory the current path is set to the directory and the index is set to 0.
    * 
    * @param path - The path to set as the current path for the
    */
    private void setCurrentPath(Path path) {
        // Set the current path to the current path.
        if(Files.isDirectory(path)) {
            currentPath = path;
            currentIndex = 0;
        }
        searchText = currentPath.toAbsolutePath().toString();
    }

    /**
    * Opens a directory in the history. If the directory is opened it is moved to the top of the history stack
    * 
    * @param path - The path to the
    */
    private void openDirectory(Path path) {
        // If path is a directory it is pushed back to history.
        if(Files.isDirectory(path)) {
            // Removes the back counter from the history.
            while(backCounter > 0) {
                history.pop();
                --backCounter;
            }
            history.push(path);
            setCurrentPath(path);

        }
    }

    /**
    * Formats a FileTime to a date / time string. This is used to generate log entries that are in the format yyyy - MM - dd.
    * 
    * @param fileTime - the file time to format. Must not be null.
    * 
    * @return the formatted date / time string null if the fileTime is null or not in the format yyyy -
    */
    private static String formatDateTime(FileTime fileTime) {

        LocalDateTime localDateTime = fileTime
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return localDateTime.format(DATE_FORMATTER);
    }
}
