import React from 'react'
import { useAppSelector, useAppDispatch } from '../../app/hooks'
import { select, fetchFriendsByFrom } from "./friendsSlice"
import { ParticipantModel } from '../types.d'
import Grid from "@material-ui/core/Grid"
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
    textbox: {
      fontSize: "32px",
      width: "64px",
      textAlign: "center"
    },
  })

type FriendsProps = {} & WithStyles<typeof styles>

function Friends({ classes }: FriendsProps) {
  const dispatch = useAppDispatch()
  React.useEffect(() => {
    dispatch(fetchFriendsByFrom())
  }, [dispatch])
  const rows: ParticipantModel[] = useAppSelector(select).feed
  return (
    <React.Fragment>
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
                  <TableCell>friends</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {rows.map((row) => (
                  <TableRow key={Math.random()}>
                    <TableCell component="th" scope="row">{row.name}</TableCell>
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

export default withStyles(styles)(Friends)
