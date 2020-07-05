const fs = require('fs');
const readline = require('readline');
const {google} = require('googleapis');
const { start } = require('repl');

const SCOPES = ['https://www.googleapis.com/auth/calendar', 'https://www.googleapis.com/auth/calendar.events'];
const TOKEN_PATH = 'token.json';

fs.readFile('credentials.json', (err, content) => {
  if (err) return console.log('Error loading client secret file:', err);
  authorize(JSON.parse(content), listEvents, getAvailabilities);
});

function authorize(credentials, callback, callback2) {
  console.log("credentials", credentials.web)
  const {client_secret, client_id, redirect_uris} = credentials.web;
  const oAuth2Client = new google.auth.OAuth2(
      client_id, client_secret, redirect_uris[0]);

  fs.readFile(TOKEN_PATH, (err, token) => {
    if (err) return getAccessToken(oAuth2Client, callback, callback2);
    oAuth2Client.setCredentials(JSON.parse(token));
    callback(oAuth2Client, callback2);
  });
}

function getAccessToken(oAuth2Client, callback, callback2) {
  const authUrl = oAuth2Client.generateAuthUrl({
    access_type: 'offline',
    scope: SCOPES,
  });
  console.log('Authorize this app by visiting this url:', authUrl);
  const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
  });
  rl.question('Enter the code from that page here: ', (code) => {
    rl.close();
    oAuth2Client.getToken(code, (err, token) => {
      if (err) return console.error('Error retrieving access token', err);
      oAuth2Client.setCredentials(token);
      // Store the token to disk for later program executions
      fs.writeFile(TOKEN_PATH, JSON.stringify(token), (err) => {
        if (err) return console.error(err);
        console.log('Token stored to', TOKEN_PATH);
      });
      callback(oAuth2Client, callback2);
    });
  });
}

function listEvents(auth, callback) {
  const calendar = google.calendar({version: 'v3', auth});
  calendar.events.list({
    calendarId: 'primary',
    timeMin: (new Date()).toISOString(),
    maxResults: 10,
    singleEvents: true,
    orderBy: 'startTime',
  }, (err, res) => {
    if (err) return console.log('The API returned an error: ' + err);
    const events = res.data.items;
    if (events.length) {
      console.log('Upcoming 10 events:');
      events.map((event, i) => {
        const start = event.start.dateTime || event.start.date;
        console.log(`${start} - ${event.summary}`);
      });
    } else {
      console.log('No upcoming events found.');
    }
  });

  callback(auth);
}

function getAvailabilities(auth) {
  const calendar = google.calendar({version: 'v3', auth});
  const startDate = new Date(Date.now());
  const numDays = 1
  const endDate = new Date(Date.now() + 1000 * 60 * 60);
  // endDate.setDate(startDate.getDate() + numDays)

  console.log("start: ", startDate, "end: ", endDate)
  calendar.freebusy.query({
    auth: auth,
    headers:  { "content-type" : "application/json" },
    resource: {
      items: [{id: 'aqureshi@sps-program.com'}, {id: "sps-program.com_psfs6lrh06kqk2hqub5hu6dj8s@group.calendar.google.com"}],
      timeMin: startDate.toISOString(),
      timeMax: endDate.toISOString()
    },
  }, (err, res) => {
    if (err) return console.log("The API returned an error: " + err);
    console.log("response: ", res.data);
    Object.keys(res.data.calendars).forEach( cal => {
      console.log(res.data.calendars[cal])
    })
  });
}
