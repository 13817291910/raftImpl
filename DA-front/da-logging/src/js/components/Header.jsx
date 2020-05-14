import React from "react";
import { Link } from "react-router-dom";

export function Header() {
  return (
    <header id="App-header">
      <span>Distributed Algorithms</span>
      <section>
        <ul>
          <li>
            <button>
              <Link to={"/enterlog"}>Enter Log</Link>
            </button>
          </li>
          <li>
            <button>
              <Link to={"/showlogs"}>Show Logs</Link>
            </button>
          </li>
        </ul>
      </section>
    </header>
  );
}
