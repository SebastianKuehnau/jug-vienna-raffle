package com.vaadin.demo.application.views;

import com.vaadin.demo.application.security.data.KeycloakUser;
import com.vaadin.demo.application.security.util.UserUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouteBaseData;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The main view is a top-level placeholder for other views.
 */
@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {

    private H1 viewTitle;

    private final AuthenticationContext authenticationContext;

    protected final Log logger = LogFactory.getLog(getClass());

    public MainLayout(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("jug-vienna-raffle");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        menuEntries.forEach(entry -> {
            SideNavItem sideNavItem = createSideNavItem(entry);
            nav.addItem(sideNavItem);
        });


        return nav;
    }

    private SideNavItem createSideNavItem(MenuEntry entry) {
        SideNavItem sideNavItem = entry.icon() != null
                ? new SideNavItem(entry.title(), entry.path(), new SvgIcon(entry.icon()))
                : new SideNavItem(entry.title(), entry.path());
        sideNavItem.setMatchNested(true);
        return sideNavItem;
    }


    public byte[] recoverImageFromUrl(String urlText) {
        try {
            URL url = new URL(urlText);
            try (InputStream is = url.openStream()) {
                return IOUtils.toByteArray(is);
            }
        } catch (IOException ioException) {
            logger.error(ioException.getMessage());
        }

        return null;
    }


    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<KeycloakUser> maybeUser = UserUtil.getCurrentUser();
        if (maybeUser.isPresent()) {
            KeycloakUser user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName());

            if (user.getAvatarUrl() != null) {
                StreamResource resource = new StreamResource("profile-pic",
                        () -> new ByteArrayInputStream(recoverImageFromUrl(user.getAvatarUrl())));

                avatar.setImageResource(resource);
            }

            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> authenticationContext.logout());

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent()).orElse("");
    }
}
