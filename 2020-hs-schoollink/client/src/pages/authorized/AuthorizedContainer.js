import React from 'react';
import { Switch, Route, BrowserRouter, Link } from 'react-router-dom';
import AuthService from '../../services/AuthService';
import Dashboard from './Dashboard';
import Assignments from './Assignments';
import Calendar from './Calendar';
import Timetable from './Timetable';
import DailyNotices from './DailyNotices';

export default class AuthorizedContainer extends React.Component {
    constructor() {
        super();
        this.state = { hidden: true, user: null };
    }

    handleNavBtnClick() {
        this.setState({hidden:!this.state.hidden})
    }

    componentDidMount() {
        fetch('api/v1/users/me', { headers: { 'Authorization': 'Bearer ' + AuthService.getAccessToken() }})
            .then(c => c.json())
            .then(d => {
                this.setState({user: d})
            })
    }

    render() {
        return <BrowserRouter>
        <div className="flex mb-4 h-screen bg-gray-200">
            {this.state.hidden ?
            <div class="w-1/7 flex flex-col h-100 bg-red-600 h-full py-4 px-4 mr-4 md:hidden">
                <nav className='flex-grow'>
                  <div>
                    <button onClick={this.handleNavBtnClick.bind(this)} type="button" class="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none focus:bg-gray-100 focus:text-gray-500 transition duration-150 ease-in-out">
                      <svg class="h-6 w-6" stroke="currentColor" fill="none" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
                      </svg>
                    </button>
                  </div>
                </nav>
            </div> : null}

            {!this.state.hidden ? 
            <div hidden class="md:hidden w-3/5 flex flex-col h-100 bg-red-600 h-full py-4 px-4 mr-4">
                <nav className='flex-grow'>
                    <button onClick={this.handleNavBtnClick.bind(this)} type="button" class="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none focus:bg-gray-100 focus:text-gray-500 transition duration-150 ease-in-out">
                      <svg class="h-6 w-6" stroke="currentColor" fill="none" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                    <Link to="/" onClick={this.handleNavBtnClick.bind(this)}>
                        <div className="flex mb-4 text-white text-xl font-bold py-2 px-4 rounded">
                            SchoolLink
                        </div>
                    </Link>
                    <Link to="/dashboard" onClick={this.handleNavBtnClick.bind(this)}>
                        <div className="text-white flex mb-4 py-2 px-4 hover:bg-red-800 text-lg font-semibold w-full rounded">
                            Dashboard
                        </div>
                    </Link>
                    <Link to="/assignments" onClick={this.handleNavBtnClick.bind(this)}>
                        <div className="text-white flex mb-4 py-2 px-4 hover:bg-red-800 text-lg font-semibold w-full rounded">
                            Assignments
                        </div>
                    </Link>
                    <Link to="/calendar" onClick={this.handleNavBtnClick.bind(this)}>
                        <div className="text-white flex mb-4 py-2 px-4 hover:bg-red-800 text-lg font-semibold w-full rounded">
                            Calendar
                        </div>
                    </Link>
                    <Link to="/timetable" onClick={this.handleNavBtnClick.bind(this)}>
                        <div className="text-white flex mb-4 py-2 px-4 hover:bg-red-800 text-lg font-semibold w-full rounded">
                            Timetable
                        </div>
                    </Link>
                    <Link to="/dailynotices" onClick={this.handleNavBtnClick.bind(this)}>
                        <div className="text-white flex mb-4 py-2 px-4 hover:bg-red-800 text-lg font-semibold w-full rounded">
                            Daily Notices
                        </div>
                    </Link>
                    <Link to="/logout" onClick={this.handleNavBtnClick.bind(this)}>
                        <div className="text-white flex mb-4 py-2 px-4 hover:bg-red-800 text-lg font-semibold w-full rounded">
                            Logout
                        </div>
                    </Link>
                </nav>
            </div> : null}

            <div className="w-1/5 flex flex-col h-100 bg-red-600 h-full py-4 px-4 mr-4 hidden md:block">
                <nav className='flex-grow'>
                    <Link to="/">
                        <div className="flex mb-4 text-white text-xl font-bold py-2 px-4 rounded">
                            SchoolLink
                        </div>
                    </Link>
                    <Link to="/dashboard">
                        <div className="text-white flex mb-4 py-2 px-4 hover:bg-red-800 text-lg font-semibold w-full rounded">
                            Dashboard
                        </div>
                    </Link>
                    <Link to="/assignments">
                        <div className="text-white flex mb-4 py-2 px-4 hover:bg-red-800 text-lg font-semibold w-full rounded">
                            Assignments
                        </div>
                    </Link>
                    <Link to="/calendar">
                        <div className="text-white flex mb-4 py-2 px-4 hover:bg-red-800 text-lg font-semibold w-full rounded">
                            Calendar
                        </div>
                    </Link>
                    <Link to="/timetable">
                        <div className="text-white flex mb-4 py-2 px-4 hover:bg-red-800 text-lg font-semibold w-full rounded">
                            Timetable
                        </div>
                    </Link>
                    <Link to="/dailynotices">
                        <div className="text-white flex mb-4 py-2 px-4 hover:bg-red-800 text-lg font-semibold w-full rounded">
                            Daily Notices
                        </div>
                    </Link>
                    <Link to="/logout">
                        <div className="text-white flex mb-4 py-2 px-4 hover:bg-red-800 text-lg font-semibold w-full rounded">
                            Logout
                        </div>
                    </Link>
                </nav>
                <Link to="/profile">
                    <div className="flex mb-4 py-2 px-4 hover:bg-red-800 border-t border-gray-200">
                        <img className="-ml-2 inline-block h-10 w-10 rounded-full shadow-solid" src={this.state.user?.avatar} alt="" />
                        <div className="px-4">
                            <span className="text-white text-sm font-medium">{this.state.user?.name ?? 'Connecting...'}</span>
                            <br />
                            <span className="text-gray-400 font-light text-sm">Settings</span>
                        </div>
                    </div>
                </Link>
                
                <div className="flex mb-4 py-1 px-1 text-gray-300 text-sm font-light">
                    SchoolLink client v1.0.241<br />
                    Connected via node SCNZ-2<br />
                    Build 2020.04.06.1.241005
                </div>
            </div>
            <div className="w-4/5 my-4">
                    <Switch>
                        <Route exact path="/" component={Dashboard} />
                        <Route exact path="/dashboard" component={Dashboard} />
                        <Route exact path="/assignments" component={Assignments} />
                        <Route exact path="/calendar" component={Calendar} />
                        <Route exact path="/timetable" component={Timetable} />
                        <Route exact path="/dailynotices" component={DailyNotices} />
                        <Route exact path="/logout" render={(props) => {
                            AuthService.logout();
                        }} />
                    </Switch>
            </div>
        </div>
    </BrowserRouter>
    }
}