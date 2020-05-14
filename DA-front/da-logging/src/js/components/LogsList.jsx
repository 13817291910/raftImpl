import React, { Component } from "react";
import { Alert, Spin, Table } from "antd";
import { connect } from "react-redux";
import { getLogs } from "../actions/log";

class LogsListComponent extends Component {
  componentDidMount() {
    this.props.getLogs();
  }

  render() {
    const { logs, isLoading, xhrStatus, alertMessage } = this.props;
    const containerStyle = isLoading
      ? { display: "flex", justifyContent: "center" }
      : {};

    const columns = [
      {
        title: "Index",
        dataIndex: "idex",
        key: "idex",
      },
      {
        title: "Term",
        dataIndex: "term",
        key: "term",
      },
      {
        title: "Text",
        dataIndex: "text",
        key: "text",
      },
    ];

    return (
      <section className={"body"} style={containerStyle}>
        {xhrStatus !== null ? (
          !xhrStatus && (
            <Alert
              message={alertMessage}
              type={"error"}
              style={{ width: "100%" }}
            />
          )
        ) : (
          <>
            <h1>Logs</h1>
            {logs.length > 0 ? (
              <Table dataSource={logs} columns={columns} />
            ) : (
              <p>Oops, there are no data available on the backend...</p>
            )}
          </>
        )}
      </section>
    );
  }
}

const mapStateToProps = (state) => {
  const { logs, isLoading, xhrStatus, alertMessage } = state.log;
  return { logs, isLoading, xhrStatus, alertMessage };
};

const mapDispatchToProps = (dispatch) => {
  return {
    getLogs: () => getLogs()(dispatch),
  };
};

const LogsList = connect(
  mapStateToProps,
  mapDispatchToProps
)(LogsListComponent);

export { LogsList };
