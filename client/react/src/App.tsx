import React from "react"
import Inbound from "./features/inbound/Inbound"
import Outbound from "./features/outbound/Outbound"
import Friends from "./features/friends/Friends"
import {
  createMuiTheme,
  withStyles,
  createStyles,
  Theme,
  WithStyles,
  StyleRules
} from "@material-ui/core/styles"
import {
  MuiThemeProvider,
  CssBaseline,
} from "@material-ui/core"
import purple from "@material-ui/core/colors/purple"
import Drawer from '@material-ui/core/Drawer'
import Divider from '@material-ui/core/Divider'
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom"

const drawerWidth = 400;

const theme = createMuiTheme({
  palette: {
    primary: purple,
    secondary: {
      main: "#fff"
    },
    background: {
      default: "#fff"
    }
  }
})

const styles: (theme: Theme) => StyleRules<string> = theme =>
  createStyles({
    root: {
      
    },
    app: {
      textAlign: "center"
    },
    appHeader: {
      minHeight: "10vh",
      display: "flex",
      flexDirection: "column",
      alignItems: "center",
      justifyContent: "center",
      fontSize: "calc(10px + 2vmin)"
    },
    drawer: {
      width: drawerWidth,
      textAlign: "left", 
      flexShrink: 0,
    },
    appLink: {
      color: "rgb(112, 76, 182)"
    }
  })

type AppProps = {} & WithStyles<typeof styles>

const App = ({ classes }: AppProps) => (
  <MuiThemeProvider theme={theme}>
    <CssBaseline />
    <div className={classes.app}>
      <header className={classes.appHeader}>
        <Router>
          <div>
            <Drawer
               className={classes.drawer}
               variant="permanent"
               classes={{
                 paper: classes.drawerPaper,
               }}
               anchor="left"
            >
              <nav>
                  <Divider />
                  <Link to="/">Inbound</Link>
                  <Divider />
                  <Link to="/outbound">Outbound</Link>
                  <Divider />
                  <Link to="/friends">Friends</Link>
              </nav>
            </Drawer>
            <Switch>
              <Route exact path="/">
                <Inbound />
              </Route>
              <Route exact path="/outbound">
                <Outbound />
              </Route>
              <Route exact path="/friends">
                <Friends />
              </Route>
            </Switch>
          </div>
        </Router>
      </header>
    </div>
  </MuiThemeProvider>
)

export default withStyles(styles)(App)
