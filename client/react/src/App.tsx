import React from 'react'
import Inbound from './features/inbound/Inbound'
import Outbound from './features/outbound/Outbound'
import OutboundSearch from './features/outbound/OutboundSearch'
import Friends from './features/friends/Friends'

import {
  createMuiTheme,
  withStyles,
  createStyles,
  Theme,
  WithStyles,
  StyleRules,
  MuiThemeProvider
} from '@material-ui/core/styles'
import CssBaseline from '@material-ui/core/CssBaseline'
import Drawer from '@material-ui/core/Drawer'
import Box from '@material-ui/core/Box'
import AppBar from '@material-ui/core/AppBar'
import Toolbar from '@material-ui/core/Toolbar'
import Typography from '@material-ui/core/Typography'
import Divider from '@material-ui/core/Divider'
import IconButton from '@material-ui/core/IconButton'
import MenuIcon from '@material-ui/icons/Menu'
import PeopleIcon from '@material-ui/icons/People'
import HearingIcon from '@material-ui/icons/Hearing'
import SearchIcon from '@material-ui/icons/Search'
import RecordVoiceOverIcon from '@material-ui/icons/RecordVoiceOver'
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft'
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from 'react-router-dom'
import purple from '@material-ui/core/colors/purple'

const drawerWidth = 240
const mainLeft = 300
const mainTop = 30

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
  toolbar: {
    paddingRight: 24, // keep right padding when drawer closed
  },
  toolbarIcon: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'flex-end',
    padding: '0 8px',
    ...theme.mixins.toolbar,
  },
  appBar: {
    zIndex: theme.zIndex.drawer + 1,
    transition: theme.transitions.create(['width', 'margin'], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
  },
  appBarShift: {
    marginLeft: drawerWidth,
    width: `calc(100% - ${drawerWidth}px)`,
    transition: theme.transitions.create(['width', 'margin'], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
  },
  menuButton: {
    marginRight: 36,
  },
  menuButtonHidden: {
    display: 'none',
  },
  drawerPaper: {
    position: 'relative',
    whiteSpace: 'nowrap',
    width: drawerWidth,
    transition: theme.transitions.create('width', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
    boxSizing: 'border-box',
  },
  drawerPaperClose: {
    overflowX: 'hidden',
    transition: theme.transitions.create('width', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
    width: theme.spacing(7),
    [theme.breakpoints.up('sm')]: {
      width: theme.spacing(9),
    },
  },
  mainArea: {
    position: 'absolute',
    top: mainTop,
    left: mainLeft
  },
  appBarSpacer: theme.mixins.toolbar,
})

type AppProps = {} & WithStyles<typeof styles>

const App = ({ classes }: AppProps) => (
  <MuiThemeProvider theme={theme}>
    <Box>
      <CssBaseline />
      <AppBar
        position="absolute"
        className={classes.appBar}
      >
        <Toolbar className={classes.toolbar}>
          <IconButton
            edge="start"
            color="inherit"
            aria-label="open drawer"
            className={classes.menuButton}
          >
            <MenuIcon />
          </IconButton>
          <Typography
            component="h1"
            variant="h6"
            color="inherit"
            noWrap
          >
            News Feed
          </Typography>
        </Toolbar>
      </AppBar>
      <Router>
        <Drawer
          variant="permanent"
          classes={{
            paper: classes.drawerPaper,
          }}
        >
          <div className={classes.toolbarIcon}>
            <IconButton>
              <ChevronLeftIcon />
            </IconButton>
          </div>
          <Divider />
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
                <Link to="/my-friends-posts">
                  <div className={classes.toolbarIcon}>
                    <IconButton>
                      <HearingIcon />
                    </IconButton>
                    Posts For Me
                  </div>
                </Link>
                <Divider />
                <Link to="/my-posts">
                  <div className={classes.toolbarIcon}>
                    <IconButton>
                      <RecordVoiceOverIcon />
                    </IconButton>
                    My Posts
                  </div>
                </Link>
                <Divider />
                <Link to="/my-friends">
                  <div className={classes.toolbarIcon}>
                    <IconButton>
                      <PeopleIcon />
                    </IconButton>
                    My Friends
                  </div>
                </Link>
                <Divider />
                <Link to="/new-friends">
                  <div className={classes.toolbarIcon}>
                    <IconButton>
                      <SearchIcon />
                    </IconButton>
                    New Friends
                  </div>
                </Link>
              </nav>
          </Drawer>
        </Drawer>
        <Box
          component="main" 
          className={classes.mainArea}
        >
          <div className={classes.appBarSpacer} />
          <Switch>
            <Route exact path="/">
              <Typography
                 component="h1"
                 variant="h6"
                 color="inherit"
              >
              Welcome to this rudimentary news feed application written in typescript on react.
              </Typography>
            </Route>
            <Route exact path="/my-friends-posts">
              <Inbound />
            </Route>
            <Route exact path="/my-posts">
              <Outbound />
            </Route>
            <Route exact path="/my-friends">
              <Friends />
            </Route>
            <Route exact path="/new-friends">
              <OutboundSearch />
            </Route>
          </Switch>
        </Box>
      </Router>
    </Box>
  </MuiThemeProvider>
)

export default withStyles(styles)(App)
