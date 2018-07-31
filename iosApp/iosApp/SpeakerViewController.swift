//
//  SpeakerViewController.swift
//  iosApp
//
//  Created by Kevin Galligan on 7/30/18.
//  Copyright © 2018 Kevin Galligan. All rights reserved.
//

import UIKit
import NotepadArchitecture
import Kingfisher

class SpeakerViewController: MaterialAppBarUIViewController, UITableViewDelegate, UITableViewDataSource {

    @IBOutlet weak var speakerImage: UIImageView!
    @IBOutlet weak var speakerName: UILabel!
    @IBOutlet weak var speakerCompany: UILabel!
    @IBOutlet weak var tableView: UITableView!
    
    var speakerId:String!
    var viewModel:NotepadArchitectureSpeakerViewModel!
    var speakerInfo:NotepadArchitectureSpeakerUiData!
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        showArrowBack()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
        viewModel = NotepadArchitectureSpeakerViewModel(sessionId: speakerId)
        viewModel.registerForChanges(proc: updateUi)
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func updateUi(speakerInfo:NotepadArchitectureSpeakerUiData) -> NotepadArchitectureStdlibUnit{
        self.speakerInfo = speakerInfo
        speakerName.text = speakerInfo.fullName
        speakerCompany.text = speakerInfo.company
        if (speakerInfo.profilePicture != nil) {
            speakerImage.kf.setImage(with: URL(string: speakerInfo.profilePicture!)!)
            speakerImage.layer.cornerRadius = 36
            speakerImage.layer.masksToBounds = true
        }
        
//        styleButton()
//        updateAllUi()
        return NotepadArchitectureStdlibUnit()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
//        return speakerInfos.count
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
//        let cell: UserTableViewCell = tableView.dequeueReusableCell(withIdentifier: "userCell") as! UserTableViewCell
//
//        let speakerInfo = speakerInfos[indexPath.row]
//        cell.loadInfo(speakerInfo)
//        cell.selectionStyle = UITableViewCellSelectionStyle.none
//        return cell
        return UITableViewCell()
    }
    
//    func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
//        return UITableViewAutomaticDimension
//    }
//
//    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
//        return UITableViewAutomaticDimension
//    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
//        tableView.deselectRow(at: indexPath, animated: false)
//
//        let selected = speakerInfos[indexPath.row]
//        openWebsite(selected)
        
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

/*class UserDetailViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, DVMUserDetailViewModel_Host {
    
    @IBOutlet weak var speakerImage: UIImageView!
    @IBOutlet weak var speakerName: UILabel!
    @IBOutlet weak var speakerCompany: UILabel!
    @IBOutlet weak var tableView: UITableView!
    
    private let GOOGLE_URL: String = "http://www.google.com/search?q="
    private let TWITTER_URL: String = "http://www.twitter.com/"
    private let GPLUS_URL: String = "http://www.plus.google.com/s/"
    private let LINKEDIN_URL: String = "http://www.linkedin.com/in/"
    private let FACEBOOK_URL: String = "http://www.facebook.com/"
    
    var speakerInfos: [SpeakerInfo] = []
    var viewModel: DVMUserDetailViewModel!
    var userId: jlong = 0
    
    
    // MARK: Lifecycle events
    override func viewDidLoad() {
        super.viewDidLoad()
        viewModel = DVMUserDetailViewModel.forIos()
        viewModel.wire(with: self, withLong: userId)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        tableView.delegate = self
        tableView.dataSource = self
        let nib = UINib(nibName: "UserTableViewCell", bundle: nil)
        tableView.register(nib, forCellReuseIdentifier: "userCell")
    }
    
    deinit {
        viewModel.unwire()
    }
    
    func findUserError() {
        
    }
    
    func onUserFound(with userAccount: DDATUserAccount) {
        showUserData(userAccount)
    }
    
    func showUserData(_ userAccount: DDATUserAccount){
        checkForSpeakerInfo(speaker: userAccount)
        
        if (!userAccount.getName().isEmpty) {
            speakerName.text = userAccount.getName()
            speakerName.isHidden = false
            speakerName.sizeToFit()
        }
        if (!userAccount.getCompany().isEmpty) {
            speakerCompany.text = userAccount.getCompany()
            speakerCompany.isHidden = false
            speakerCompany.sizeToFit()
        }
        
        speakerImage.kf.setImage(with: URL(string: (userAccount.avatarImageUrl())!))
        speakerImage.layer.cornerRadius = 24
        speakerImage.layer.masksToBounds = true
        speakerImage.sizeToFit()
        tableView.reloadData()
    }
    
    func showError(_ err:String){
        let alert = UIAlertController(title: "Problems", message: err, preferredStyle: UIAlertControllerStyle.alert)
        alert.addAction(UIAlertAction(title: "Click", style: UIAlertActionStyle.default, handler: nil))
        self.present(alert, animated: true, completion: nil)
    }
    
    func checkForSpeakerInfo(speaker: DDATUserAccount) {
        let speakerCompany = speaker.getCompany()
        if speakerCompany.isNotNilOrEmpty() {
            speakerInfos.append(SpeakerInfo(type: .company, info: speakerCompany!))
        }
        
        let speakerWebsite = speaker.getWebsite()
        if speakerWebsite.isNotNilOrEmpty() {
            speakerInfos.append(SpeakerInfo(type: .website, info: speakerWebsite!))
        }
        
        let speakerFb = speaker.getFacebook()
        if speakerFb.isNotNilOrEmpty() {
            speakerInfos.append(SpeakerInfo(type: .facebook, info: speakerFb!))
        }
        
        let speakerTwitter = speaker.getTwitter()
        if speakerTwitter.isNotNilOrEmpty() {
            speakerInfos.append(SpeakerInfo(type: .twitter, info: speakerTwitter!))
        }
        
        let speakerLinkedin = speaker.getLinkedIn()
        if speakerLinkedin.isNotNilOrEmpty() {
            speakerInfos.append(SpeakerInfo(type: .linkedin, info: speakerLinkedin!))
        }
        
        let speakerGPlus = speaker.getgPlus()
        if speakerGPlus.isNotNilOrEmpty() {
            speakerInfos.append(SpeakerInfo(type: .gplus, info: speakerGPlus!))
        }
        
        let speakerProfile = speaker.getProfile()
        if speakerProfile.isNotNilOrEmpty() {
            speakerInfos.append(SpeakerInfo(type: .profile, info: speakerProfile!))
        }
    }
    
    
    
    func openWebsite(_ selected: SpeakerInfo) {
        var url = ""
        
        if selected.info.contains(".com") {
            url = selected.info.formatUrl()
        } else {
            switch (selected.type) {
            case .company:
                url = GOOGLE_URL + selected.info
                break
            case .website:
                url = selected.info.formatUrl()
                break
            case .facebook:
                url = FACEBOOK_URL + selected.info
                break
            case .twitter:
                url = TWITTER_URL + selected.info
                break
            case .linkedin:
                url = LINKEDIN_URL + selected.info
                break
            case .gplus:
                url = GPLUS_URL + selected.info.replacingOccurrences(of: "+", with: "")
                break
            default:
                break
            }
        }
        
        if (!url.isEmpty) {
            UIApplication.shared.open(URL(string: url)!)
        }
    }
    
    // MARK: TableView
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return speakerInfos.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: UserTableViewCell = tableView.dequeueReusableCell(withIdentifier: "userCell") as! UserTableViewCell
        
        let speakerInfo = speakerInfos[indexPath.row]
        cell.loadInfo(speakerInfo)
        cell.selectionStyle = UITableViewCellSelectionStyle.none
        return cell
    }
    
    func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
        
        let selected = speakerInfos[indexPath.row]
        openWebsite(selected)
        
    }
}*/
