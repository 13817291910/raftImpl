import React, { Component } from "react";
import { Input, Button, Alert } from "antd";
import { connect } from "react-redux";

import { updateLogText, postNewLog } from "../actions/log";

const { TextArea } = Input;

class LogFormComponent extends Component {
  updateLog = (log) => this.props.updateLogText(log);

  render() {
    const {
      isLoading,
      newLog,
      createNewLog,
      xhrStatus,
      alertMessage,
    } = this.props;
    return (
      <section className={"body"}>
        <h1>Log Form</h1>
        <span>Please type in the logs here, and click "Submit" button.</span>
        {newLog && (
          <>
            <h2>New Log: </h2>
            <p>{newLog}</p>
          </>
        )}

        {xhrStatus !== null && (
          <Alert
            message={alertMessage}
            type={xhrStatus ? "success" : "error"}
            style={{ width: "100%", marginTop: "1rem" }}
          />
        )}
        <form style={{ marginTop: "1rem" }}>
          <TextArea
            style={{ marginBottom: "1rem" }}
            rows={10}
            value={newLog}
            onChange={(event) => this.updateLog(event.target.value)}
          />
          <Button
            loading={isLoading}
            style={{ float: "right" }}
            type={"primary"}
            onClick={() => createNewLog(newLog)}
          >
            Submit
          </Button>
        </form>
      </section>
    );
  }
}

const mapStateToProps = (state) => {
  const { isLoading, newLog, xhrStatus, alertMessage } = state.log;
  return { isLoading, newLog, xhrStatus, alertMessage };
};

const mapDispatchToProps = (dispatch) => {
  return {
    updateLogText: (logText) => dispatch(updateLogText(logText)),
    createNewLog: (newLog) => postNewLog(newLog)(dispatch),
  };
};

const LogForm = connect(mapStateToProps, mapDispatchToProps)(LogFormComponent);

export { LogForm };
