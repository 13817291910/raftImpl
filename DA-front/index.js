const express = require("express");
const bodyParser = require("body-parser");

// Create an express instance named as app.
const app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

require("./routes/apiRoutes")(app);

if (process.env.NODE_ENV === "production") {
  // Express will serve up production assets like main.js or main.css file.
  app.use(express.static("client/build"));

  // Express will serve up index.html file if it does not recognize the route.
  const path = require("path");
  app.get("*", (req, res) => {
    res.sendFile(path.resolve(__dirname, "client", "build", "index.html"));
  });
}

const PORT = process.env.PORT || 6000;
app.listen(PORT);
