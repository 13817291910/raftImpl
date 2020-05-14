import {
  UPDATE_LOG_TEXT,
  UPDATE_LOADING_STATUS,
  UPDATE_XHR_STATUS,
  RETRIEVE_LOGS_SUCCESSFULLY,
} from "../actions/types.d";

const LOG_REDUCER_DEFAULT_VALUE = {
  isLoading: false,
  newLog: "",
  xhrStatus: null,
  alertMessage: "",
  logs: [],
};

export function logReducer(state = LOG_REDUCER_DEFAULT_VALUE, action) {
  switch (action.type) {
    case UPDATE_LOADING_STATUS:
    case UPDATE_LOG_TEXT:
    case UPDATE_XHR_STATUS:
    case RETRIEVE_LOGS_SUCCESSFULLY:
      return { ...state, ...action.payload };
    default:
      return state;
  }
}
