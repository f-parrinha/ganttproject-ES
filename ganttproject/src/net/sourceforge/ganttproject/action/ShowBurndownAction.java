package net.sourceforge.ganttproject.action;

import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.gui.burndown.BurndownPopup;

import java.awt.event.ActionEvent;
//pixa maxima
public class ShowBurndownAction extends GPAction {
    private final UIFacade myUiFacade;

    public ShowBurndownAction(UIFacade uifacade) {
        super("about");
        myUiFacade = uifacade;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BurndownPopup agp = new BurndownPopup(myUiFacade);
        agp.show();
    }
}
