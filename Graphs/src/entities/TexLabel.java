package entities;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import org.jfree.fx.FXGraphics2D;
import org.scilab.forge.jlatexmath.ParseException;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

public class TexLabel extends Canvas {

    public static final String DEFAULT = "\\infty";

    private FXGraphics2D gc;
    private TeXIcon icon;
    private TextField input;

    TexLabel(){
        super();
        this.gc = new FXGraphics2D(getGraphicsContext2D());

        input = new TextField();
        setText(DEFAULT);
    }

    String setText(String text) {
        // create a formula
        TeXFormula formula = null;

        if (text.isEmpty()) text = DEFAULT;

        try {
            formula = new TeXFormula(text);
        }catch(ParseException e){
            formula = new TeXFormula(DEFAULT);
            text = DEFAULT;
        }
        String curText = text;

        if(!text.equals("\\infty"))
            icon = formula.createTeXIcon(TeXConstants.ALIGN_CENTER,17);
        else
            icon = formula.createTeXIcon(TeXConstants.ALIGN_CENTER,22);


        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        double width = icon.getIconWidth();
        double height = icon.getIconHeight();

        setWidth(width);
        setHeight(height);

        gc.clearRect(0, 0, width, height);

        FXGraphics2D graphics = new FXGraphics2D(gc);
        icon.paintIcon(null, graphics, 0, 0);
        return curText;
    }

    private void checkText(String text){
        boolean isCommand = false;

    }


    public void showInput(){

    }
}
