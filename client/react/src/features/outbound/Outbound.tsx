import React from 'react'
import { useAppSelector, useAppDispatch } from '../../app/hooks'
import { select, fetchOutboundByFrom } from "./outboundSlice"
import { OutboundModel, OutboundApi, Util } from '../types.d'
import Grid from '@material-ui/core/Grid'
import Card from '@material-ui/core/Card'
import CardContent from '@material-ui/core/CardContent'
import Button from '@material-ui/core/Button'
import TextField from '@material-ui/core/TextField'
import Dialog from '@material-ui/core/Dialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogContentText from '@material-ui/core/DialogContentText'
import DialogTitle from '@material-ui/core/DialogTitle'
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
  submitted: {
    fontSize: 14,
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

type OutboundProps = {} & WithStyles<typeof styles>

interface OutboundParentProps {
  refresh(): void
}

function AddOutboundForm(props: OutboundParentProps) {
  let subject: string = ''
  let story: string = ''
  
  const [open, setOpen] = React.useState(false)

  const handleClickOpen = () => {
    setOpen(true)
  }

  const handleSubmit = () => {
    setOpen(false)
    const u: Util = Util.getInstance()
    const d: Date = new Date()
    const df: string = d.toISOString()
    const o: OutboundModel = new OutboundModel(df, subject, story)
    OutboundApi.getInstance(u).add(o)
    setTimeout(props.refresh, 2000)
  }
  
  const handleClose = () => {
    setOpen(false)
  }

  const handleSubjectChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    subject = event.target.value
  }

  const handleStoryChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    story = event.target.value
  }
  
  return (
    <div>
      <Button variant="outlined" color="primary" onClick={handleClickOpen}>
        Post New Item
      </Button>
      <Dialog open={open} onClose={handleClose} aria-labelledby="form-dialog-title">
        <DialogTitle id="form-dialog-title">Publish News Feed Item</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Broadcast a news feed item to your friends.
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            onChange={handleSubjectChange}
            id="subject"
            label="Subject"
            type="text"
            fullWidth
          />
          <TextField
            margin="dense"
            onChange={handleStoryChange}
            id="story"
            label="Story"
            type="text"
            fullWidth
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} color="primary">
            Cancel
          </Button>
          <Button onClick={handleSubmit} color="primary">
            Create
          </Button>
        </DialogActions>
      </Dialog>
    </div>  
  )
}

function Outbound({ classes }: OutboundProps) {
  const dispatch = useAppDispatch()
  React.useEffect(() => {
    dispatch(fetchOutboundByFrom())
  }, [dispatch])
  const rows: Array<OutboundModel> = useAppSelector(select).feed
  const forceUpdate = () => dispatch(fetchOutboundByFrom())
  return (
    <React.Fragment>
      <Grid container xs={12} alignItems="center" justify="center" spacing={3}>
        <Grid item xs={6} >
          This is the list of your news feed posts to your friends.
        </Grid>
        <Grid item xs={6} >
          <AddOutboundForm refresh={forceUpdate} />
        </Grid>
        <Grid item xs={12} ></Grid>
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

export default withStyles(styles)(Outbound)
