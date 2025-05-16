package com.vaadin.demo.application.adapter.in.views.admin.components;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.server.VaadinService;

@Tag("dotlottie-wc")
@NpmPackage(value = "@lottiefiles/dotlottie-wc", version = "0.2.21")
@JsModule("@lottiefiles/dotlottie-wc/dist/index.js")
public class ConfettiComponent extends Component implements ClickNotifier<ConfettiComponent>, HasSize {

    public ConfettiComponent(String animationPath, boolean autoplay, boolean loop, String width, String height) {

        getElement().setAttribute("src", createFullPath(animationPath));
        getElement().setAttribute("autoplay", autoplay);
        getElement().setAttribute("loop", loop);
        getElement().setAttribute("background", "transparent");
        if (width != null) {
            setWidth(width);
        }
        if (height != null) {
            setHeight(height);
        }
    }

    private String createFullPath(String animationPath) {
        String contextPath = VaadinService.getCurrentRequest().getContextPath();
        // If the animation path already starts with the context path, don't add it again
        if (!contextPath.isEmpty() && animationPath.startsWith(contextPath)) {
            return animationPath;
        }
        return contextPath + animationPath;
    }

    public void play() {
        getElement().executeJs("this.dotLottie.play()");
    }

    public void pause() {
        getElement().executeJs("this.dotLottie.pause()");
    }

    public void setMode(Mode mode) {
        getElement().setAttribute("mode", mode.getValue());
    }

    public void setSpeed(double speed) {
        getElement().setAttribute("speed", String.valueOf(speed));
    }

    public void setLoop(boolean loop) {
        getElement().setAttribute("loop", String.valueOf(loop));
    }

    public void setAutoplay(boolean autoplay) {
        getElement().setAttribute("autoplay", String.valueOf(autoplay));
    }

    public void setSegments(int start, int end) {
        getElement().setAttribute("segments", "["+start+","+end+"]");
    }

    /** Make the animation full overlay but don't block the clicks.
     */
    public void makeFullOverlay() {
        getStyle().set("position","fixed");
        getStyle().set("top","0");
        getStyle().set("left","0");
        getStyle().set("bottom","0");
        getStyle().set("right","0");
        getStyle().set("pointer-events","none");
    }

    public void makeRelative(String top, String left) {
        getStyle().set("position","relative");
        getStyle().set("top",top);
        getStyle().set("left",left);
    }


    /** Animation play modes.
     */
    public enum Mode {
        FORWARD("forward"),
        REVERSE("reverse"),
        BOUNCE("bounce"),
        REVERSE_BOUNCE("reverse-bounce");

        private final String value;

        Mode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
