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
import Table from '@material-ui/core/Table'
import TableBody from '@material-ui/core/TableBody'
import TableCell from '@material-ui/core/TableCell'
import TableContainer from '@material-ui/core/TableContainer'
import TableHead from '@material-ui/core/TableHead'
import TableRow from '@material-ui/core/TableRow'
import Paper from '@material-ui/core/Paper'

const styles: (theme: Theme) => StyleRules<string> = theme =>
  createStyles({
    value: {
      fontSize: "78px",
      fontFamily: "'Courier New', Courier, monospace"
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
    },
    textbox: {
      fontSize: "32px",
      width: "64px",
      textAlign: "center"
    },
    asyncButton: {
      "&:after": {
        content: "",
        backgroundColor: "rgba(112, 76, 182, 0.15)",
        display: "block",
        position: "absolute",
        width: "100%",
        height: "100%",
        left: "0",
        top: "0",
        opacity: "0",
        transition: "width 1s linear, opacity 0.5s ease 1s"
      },
      "&:active:after": {
        width: "0%",
        opacity: "1",
        transition: "0s"
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
        <Grid item >
          <TableContainer component={Paper}>
            <Table className={classes.table} aria-label="simple table">
              <TableHead>
                <TableRow>
                  <TableCell>occurred</TableCell>
                  <TableCell>subject</TableCell>
                  <TableCell>story</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {rows.map((row) => (
                  <TableRow key={Math.random()}>
                    <TableCell component="th" scope="row">{row.occurred}</TableCell>
                    <TableCell>{row.subject}</TableCell>
                    <TableCell>{row.story}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
	</Grid>
      </Grid>
    </React.Fragment>
  )
}

export default withStyles(styles)(Outbound)
