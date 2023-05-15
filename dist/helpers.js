"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.getError = function (message, error) {
    if (error === void 0) { error = undefined; }
    var obj = {
        message: message,
        error: error
    };
    return obj;
};
exports.getResponse = function (response) {
    var obj = {
        value: response
    };
    return obj;
};
