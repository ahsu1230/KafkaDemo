import React from "react";
import { Transition } from "react-transition-group";
import "./syncing.sass";
import srcSynced from "../assets/checkmark.svg";
import srcSyncing from "../assets/loading.svg";

class SyncNotif extends React.Component {

    renderSyncing = () => {
        return (
            <div className="content syncing">
                <div className="image-wrapper">
                    <img src={srcSyncing}/>
                </div>
                <h5>Syncing...</h5>
            </div>
        );   
    }

    renderSynced = () => {
        return (
            <div className="content synced">
                <div className="image-wrapper">
                    <img src={srcSynced}/>
                </div>
                <h5>Up to date!</h5>
            </div>
        );
    }


    render() {
        const isSyncing = !!this.props.syncing;
        let content = isSyncing ? this.renderSyncing() : this.renderSynced();
        return (
            <div id="syncer">{content}</div>
        );
    }
}

export default SyncNotif;
