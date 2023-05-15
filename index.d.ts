type Options = {
    action?: string,
    uri?: string,
    extra?: Object,
};

declare type startActivityForResult = (key: string, options: Options) => void

export = startActivityForResult
