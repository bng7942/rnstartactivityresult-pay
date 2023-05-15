"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
var react_native_1 = require("react-native");
var react_1 = __importDefault(require("react"));
var helpers_1 = require("./helpers");
var constants_1 = require("./constants");
var IntentModule = react_native_1.NativeModules.RNIntentModule;
exports.openIntent = function (intentParams) {
    return new Promise(function (resolve, reject) {
        try {
            if (!IntentModule) {
                reject(helpers_1.getError(constants_1.RESPONSE_MESSAGE.MODULE_NOT_FOUND));
            }
            if (!intentParams.action) {
                reject(helpers_1.getError(constants_1.RESPONSE_MESSAGE.NO_ACTION));
            }
            var extraKeys = intentParams.extra
                ? Object.keys(intentParams.extra)
                : [];
            var extraValues_1 = [];
            extraKeys.forEach(function (thisKey) {
                if (intentParams.extra) {
                    extraValues_1.push(intentParams.extra[thisKey]);
                }
                else {
                    reject(helpers_1.getError(constants_1.RESPONSE_MESSAGE.EXTRA_KEY_PAIR_ERROR));
                }
            });
            var extraKeysToSend = extraKeys.length
                ? extraKeys
                : constants_1.INTENT_PARAM_DEFAULT.EXTRA_KEY;
            var extraValuesToSend = extraValues_1.length
                ? extraValues_1
                : constants_1.INTENT_PARAM_DEFAULT.EXTRA_VALUE;
            var requestCode = intentParams.requestCode
                ? intentParams.requestCode.toString()
                : constants_1.INTENT_PARAM_DEFAULT.REQUEST_CODE;
            var type = intentParams.type || constants_1.INTENT_PARAM_DEFAULT.TYPE;
            var title = intentParams.title || constants_1.INTENT_PARAM_DEFAULT.TITLE;
            IntentModule.openIntent(intentParams.action, title, extraKeysToSend, extraValuesToSend, requestCode, // as string, else type casting fails in bridge bcoz of bug in react-native: converts number to double
            type)
                .then(function (response) {
                resolve(helpers_1.getResponse(response));
            })
                .catch(function (error) {
                reject(helpers_1.getError(constants_1.RESPONSE_MESSAGE.RESPONSE_FAIL, error));
            });
        }
        catch (err) {
            reject(helpers_1.getError(constants_1.RESPONSE_MESSAGE.UNEXPECTED_ERROR, err));
        }
    });
};
var OpenIntentComponent = /** @class */ (function (_super) {
    __extends(OpenIntentComponent, _super);
    function OpenIntentComponent() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.responseHandler = function (intentData) {
            if (!_this.props.onResponse) {
                return;
            }
            _this.props.onResponse(intentData);
        };
        _this.invokeHandler = function () {
            exports.openIntent(_this.props)
                .then(_this.responseHandler)
                .catch(_this.responseHandler);
        };
        return _this;
    }
    OpenIntentComponent.prototype.componentWillUpdate = function (nextProps) {
        if (!this.props.invoke && nextProps.invoke === true) {
            this.invokeHandler();
        }
    };
    OpenIntentComponent.prototype.render = function () {
        return null; // No UI
    };
    return OpenIntentComponent;
}(react_1.default.Component));
exports.OpenIntentComponent = OpenIntentComponent;
exports.default = exports.openIntent;
