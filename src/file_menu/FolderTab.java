package file_menu;

import general.Program;

import java.io.File;

public class FolderTab extends FileTab {

    public FolderTab(Program program, File file) {
        super(program, file);
    }

    @Override
    public void clickEvent() {

        program.getFileMenu().resetTo(file.getPath());

    }

}
