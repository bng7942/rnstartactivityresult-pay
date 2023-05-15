import React from "react";
import { IntentParams, IntentResponse, IntentParamsComponent } from "./interface";
export declare const openIntent: (intentParams: IntentParams) => Promise<IntentResponse>;
export declare class OpenIntentComponent extends React.Component<IntentParamsComponent> {
    responseHandler: (intentData: Object) => void;
    invokeHandler: () => void;
    componentWillUpdate(nextProps: IntentParamsComponent): void;
    render(): null;
}
export default openIntent;
