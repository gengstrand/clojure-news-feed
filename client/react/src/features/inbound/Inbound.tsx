import React from 'react'
import useWebSocket from 'react-use-websocket'
import { useAppSelector, useAppDispatch } from '../../app/hooks'
import { select, fetchInboundByFrom } from "./inboundSlice"
import { InboundModel, Util } from '../types.d'
import Grid from "@material-ui/core/Grid"
import Card from '@material-ui/core/Card'
import CardContent from '@material-ui/core/CardContent'
import Typography from '@material-ui/core/Typography'
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
  moniker: {
    fontSize: 14,
  },
  submitted: {
    fontSize: 10,
  },
  subject: {
    fontSize: 16,
  },
  story: {
    fontSize: 12,
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
    onMessage: (msg: MessageEvent) => {
       if (msg.data === 'changed') {
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
          <Card className={classes.root} variant="outlined">
            <CardContent>
              <Typography className={classes.subject} color="textSecondary" gutterBottom>
                {row.subject}
              </Typography>
              <Typography className={classes.moniker} color="textSecondary">
                {row.from.name}
              </Typography>
              <Typography className={classes.submitted} color="textSecondary">
                {row.occurred}
              </Typography>
              <Typography variant="body2" component="p">
                {row.story}
              </Typography>
            </CardContent>
          </Card>
        ))}
      </Grid>
    </React.Fragment>
  )
}

export default withStyles(styles)(Inbound)
