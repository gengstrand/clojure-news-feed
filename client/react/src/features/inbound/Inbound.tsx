import React from 'react'
import useWebSocket from 'react-use-websocket'
import { useAppSelector, useAppDispatch } from '../../app/hooks'
import { select, fetchInboundByFrom } from "./inboundSlice"
import { InboundModel, Util } from '../types.d'
import Grid from "@material-ui/core/Grid"
import {
  withStyles,
  Theme,
  StyleRules,
  createStyles,
  WithStyles
} from "@material-ui/core"
import Paper from '@material-ui/core/Paper'

const styles: (theme: Theme) => StyleRules<string> = theme =>
  createStyles({
  root: {
    flexGrow: 1,
  },
  paper: {
    padding: theme.spacing(2),
    textAlign: 'center',
    color: theme.palette.text.secondary,
  },
    button: {
      color: "rgb(112, 76, 182)",
      appearance: "none",
      background: "none",
      fontSize: "calc(16px + 2vmin)",
      paddingLeft: "12px",
      paddingRight: "12px",
      paddingBottom: "4px",
      cursor: "pointer",
      backgroundColor: "rgba(112, 76, 182, 0.1)",
      borderRadius: "2px",
      transition: "all 0.15s",
      outline: "none",
      border: "2px solid transparent",
      textTransform: "none",
      "&:hover": {
        border: "2px solid rgba(112, 76, 182, 0.4)"
      },
      "&:focus": {
        border: "2px solid rgba(112, 76, 182, 0.4)"
      },
      "&:active": {
        backgroundColor: "rgba(112, 76, 182, 0.2)"
      }
   }
})

type InboundProps = {} & WithStyles<typeof styles>

function Inbound({ classes }: InboundProps) {
  const dispatch = useAppDispatch()
  React.useEffect(() => {
    dispatch(fetchInboundByFrom())
  }, [dispatch])
  const rows: InboundModel[] = useAppSelector(select).feed
  const forceUpdate = () => dispatch(fetchInboundByFrom())
  const u = Util.getInstance()
  const { sendMessage } = useWebSocket('ws://127.0.0.1:8080/inbound/stream', {
    onMessage: (msg: string) => {
       if (msg === 'changed') {
          forceUpdate()
       }
    }
  })
  sendMessage(String(u.getId()))
  return (
    <React.Fragment>
      <Grid container xs={12} alignItems="center" justify="center" spacing={3}>
        <Grid item >
          This is the list of news feed posts from your friends.
        </Grid>
      </Grid>
      <Grid
        container
        xs={12}
        direction="row"
        alignItems="center"
        justify="center"
        spacing={3}
      >
        {rows.map((row) => (
          <Grid container xs={12} spacing={3} justify="center">
            <Grid item xs={4}>
              <Paper className={classes.paper}>{row.from.name}</Paper>
            </Grid>
            <Grid item xs={4}>
              <Paper className={classes.paper}>{row.occurred}</Paper>
            </Grid>
            <Grid item xs={4}>
              <Paper className={classes.paper}>{row.subject}</Paper>
            </Grid>
            <Grid item xs={12}>
              <Paper className={classes.paper}>{row.story}</Paper>
            </Grid>
            <Grid item xs={12}></Grid>
          </Grid>
        ))}
      </Grid>
    </React.Fragment>
  )
}

export default withStyles(styles)(Inbound)
