import React from "react";
import { BrowserRouter as Router, Route } from "react-router-dom";

import "../css/App.css";
import "antd/dist/antd.css";
import { Header } from "./components/Header";
import { LogsList } from "./components/LogsList";
import { LogForm } from "./components/LogForm";

function App() {
  return (
    <Router>
      <div className="App">
        <Header />
        <Route exact path={"/"} component={LogsList} />
        <Route exact path={"/showlogs"} component={LogsList} />
        <Route exact path={"/enterlog"} component={LogForm} />
      </div>
    </Router>
  );
}

export default App;
