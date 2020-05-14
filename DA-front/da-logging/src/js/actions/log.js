import { API__CREATE_LOGS, API__GET_LOGS } from "./api.d";
import {
  UPDATE_LOG_TEXT,
  UPDATE_LOADING_STATUS,
  UPDATE_XHR_STATUS,
  RETRIEVE_LOGS_SUCCESSFULLY,
} from "./types.d";

function updateLoadingStatus(isLoading) {
  return { type: UPDATE_LOADING_STATUS, payload: { isLoading } };
}

export function updateLogText(newLog = "") {
  return { type: UPDATE_LOG_TEXT, payload: { newLog } };
}

function updateXHRStatus(
  xhrStatus = true,
  alertMessage = "Log posted successfully"
) {
  return {
    type: UPDATE_XHR_STATUS,
    payload: { xhrStatus, alertMessage },
  };
}

export function postNewLog(text) {
  return (dispatch) => {
    dispatch(updateLoadingStatus(true));

    return fetch(API__CREATE_LOGS, {
      method: "POST",
      headers: {
        "Content-Type": "text/plain",
      },
      // body: JSON.stringify({ text }),
      body: JSON.stringify(text),
    })
      .then((res) => {
        if (res.ok) {
          return res.json();
        }

        throw res.statusText;
      })
      .then((data) => {
        console.log("data received =", data);
        dispatch(updateLogText());
        setTimeout(() => dispatch(updateLoadingStatus(false)), 500);

        dispatch(updateXHRStatus());
        // Hide the status alert after 3s.
        setTimeout(() => dispatch(updateXHRStatus(null)), 3000);
      })
      .catch((errorMessage) => {
        setTimeout(() => dispatch(updateLoadingStatus(false)), 500);
        dispatch(updateXHRStatus(false, errorMessage));
      });
  };
}

export function getLogs() {
  return (dispatch) => {
    dispatch(updateLoadingStatus(true));

    return fetch(API__GET_LOGS)
      .then((res) => {
        if (res.ok) {
          return res.json();
        }

        throw res.statusText;
      })
      .then((logs) => {
        console.log("data received =", logs);
        dispatch(updateLoadingStatus(false));

        dispatch({ type: RETRIEVE_LOGS_SUCCESSFULLY, payload: { logs } });
      })
      .catch((errorMessage) => {
        setTimeout(() => dispatch(updateLoadingStatus(false)), 500);
        dispatch(updateXHRStatus(false, errorMessage));
      });
  };
}
