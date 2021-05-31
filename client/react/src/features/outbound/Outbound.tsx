import React from 'react'
import { useAppSelector, useAppDispatch } from '../../app/hooks'
import { select, fetchOutboundByFrom } from "./outboundSlice"
import { OutboundModel } from '../types.d'
import Grid from "@material-ui/core/Grid"
import Button from "@material-ui/core/Button"
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

type OutboundProps = {} & WithStyles<typeof styles>

function Outbound({ classes }: OutboundProps) {
  const dispatch = useAppDispatch()
  React.useEffect(() => {
    dispatch(fetchOutboundByFrom(2))
  }, [dispatch])
  const rows: Array<OutboundModel> = useAppSelector(select).feed
  return (
    <React.Fragment>
      <Grid container xs={12} alignItems="center" justify="center" spacing={3}>
        <Grid item >
          <Button
            className={classes.button}
            aria-label="Add"
            onClick={() => console.log('TODO: add outbound')}
          >
            Add
          </Button>
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
            <Grid item xs={6}>
              <Paper className={classes.paper}>{row.occurred}</Paper>
            </Grid>
            <Grid item xs={6}>
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

export default withStyles(styles)(Outbound)
