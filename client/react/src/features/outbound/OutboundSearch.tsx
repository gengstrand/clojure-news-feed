import React from 'react'
import { useAppSelector, useAppDispatch } from '../../app/hooks'
import { select, searchOutboundByKeywords } from "./outboundSearchSlice"
import { SearchResultModel, ParticipantModel, FriendsApi, Util } from '../types.d'
import Card from '@material-ui/core/Card'
import CardActions from '@material-ui/core/CardActions'
import CardContent from '@material-ui/core/CardContent'
import TextField from '@material-ui/core/TextField'
import Button from '@material-ui/core/Button'
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
    fontSize: 16,
  },
  submitted: {
    fontSize: 14,
  },
  subject: {
    fontSize: 12,
  },
  story: {
    fontSize: 10,
  },
  pos: {
    marginBottom: 12,
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

type OutboundSearchProps = {} & WithStyles<typeof styles>

function OutboundSearch({ classes }: OutboundSearchProps) {
  let keywords: string = ''
  const dispatch = useAppDispatch()
  const rows: Array<SearchResultModel> = useAppSelector(select).matches
  const forceUpdate = () => {
    const u = Util.getInstance()
    u.setKeywords(keywords)
    dispatch(searchOutboundByKeywords())
  }
  const handleFriendClick = (p: ParticipantModel) => {
    const u: Util = Util.getInstance()
    FriendsApi.getInstance(u).add(p)
  }
  const handleKeywordsChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    keywords = event.target.value
  }
  return (
    <React.Fragment>
      <form className={classes.root} noValidate autoComplete="off">
        <TextField id="keywords" label="story keywords" onChange={handleKeywordsChange} />
        <Button onClick={forceUpdate} color="primary">Search</Button>
      </form>
      {rows && rows.map((row) => (
        <Card className={classes.root} variant="outlined">
          <CardContent>
            <Typography className={classes.moniker} color="textSecondary" gutterBottom>
              {row.participant.name}
            </Typography>
            <Typography className={classes.submitted} color="textSecondary">
              {row.outbound.occurred}
            </Typography>
            <Typography className={classes.subject} color="textSecondary">
              {row.outbound.subject}
            </Typography>
            <Typography variant="body2" component="p">
              {row.outbound.story}
            </Typography>
          </CardContent>
          <CardActions>
            <Button size="small" onClick={() => handleFriendClick(row.participant)}>Friend</Button>
          </CardActions>
        </Card>
      ))}
    </React.Fragment>
  )
}

export default withStyles(styles)(OutboundSearch)
