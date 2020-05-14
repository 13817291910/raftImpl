module.exports = (app) => {
  app.post("/api/send", (req, res) => {
    // console.log("request for /api/send is received , req =", req);
    console.log("req.body =", req.body);

    res.send({ commonMessage: "good job" });
    // res.sendStatus(403);
  });

  app.get("/api/find", (req, res) => {
    // res.sendStatus(403);
    const logs = [
      { idex: 1, term: 1, text: "jstql" },
      { idex: 2, term: 2, text: "bwgtql" },
      { idex: 3, term: 3, text: "gltql" },
      { idex: 4, term: 4, text: "datql" },
    ];

    setTimeout(() => res.send(logs), 000);
  });
};
