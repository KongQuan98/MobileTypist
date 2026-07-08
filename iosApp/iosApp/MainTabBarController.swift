//
//  MainTabBarController.swift
//  iosApp
//
//  Created by Kong Quan Lye on 13/04/2026.
//


import UIKit
import ComposeApp

class MainTabBarController: UITabBarController {
    override func viewDidLoad() {
        super.viewDidLoad()

        delegate = self

        // Define a shared toggle closure to handle visibility across all tabs
        // Kotlin Boolean is bridged as KotlinBoolean in Swift
        let onToggle: (KotlinBoolean) -> Void = { [weak self] isVisible in
            // Convert KotlinBoolean to Swift Bool using .boolValue
            self?.tabBar.isHidden = !isVisible.boolValue
        }
        
        let homeVC = createTab(
            title: "Home",
            imageName: "house.fill",
            tag: 0,
            viewController: MainViewControllerKt.HomeTabViewController(onToggleTabBar: onToggle)
        )
        let statsVC = createTab(
            title: "Stats",
            imageName: "chart.bar.fill",
            tag: 1,
            viewController: MainViewControllerKt.StatisticsTabViewController(onToggleTabBar: onToggle)
        )
        let settingsVC = createTab(
            title: "Settings",
            imageName: "gearshape.fill",
            tag: 2,
            viewController: MainViewControllerKt.SettingsTabViewController(onToggleTabBar: onToggle)
        )
        let profileVC = createTab(
            title: "Profile",
            imageName: "person.fill",
            tag: 3,
            viewController: MainViewControllerKt.ProfileTabViewController(onToggleTabBar: onToggle)
        )
        let leaderboardVC = createTab(
            title: "Board",
            imageName: "trophy.fill",
            tag: 4,
            viewController: MainViewControllerKt.LeaderboardTabViewController(onToggleTabBar: onToggle)
        )

        viewControllers = [settingsVC, leaderboardVC, homeVC, statsVC, profileVC]
        selectedIndex = 2

        setupAppearance()
    }

    private func createTab(
        title: String,
        imageName: String,
        tag: Int,
        viewController vc: UIViewController
    ) -> UIViewController {
        vc.tabBarItem = UITabBarItem(
            title: title,
            image: UIImage(systemName: imageName),
            tag: tag
        )

        return vc
    }

    private func setupAppearance() {
        let appearance = UITabBarAppearance()
        appearance.configureWithDefaultBackground()
        appearance.backgroundColor = .clear
        appearance.backgroundEffect = UIBlurEffect(style: .systemUltraThinMaterial)

        tabBar.standardAppearance = appearance
        tabBar.scrollEdgeAppearance = appearance
        tabBar.isTranslucent = true
        tabBar.layer.cornerRadius = 24
        tabBar.layer.cornerCurve = .continuous
        tabBar.clipsToBounds = true
    }
}

extension MainTabBarController: UITabBarControllerDelegate {
    func tabBarController(
        _ tabBarController: UITabBarController,
        animationControllerForTransitionFrom fromVC: UIViewController,
        to toVC: UIViewController
    ) -> UIViewControllerAnimatedTransitioning? {
        guard let viewControllers = self.viewControllers else {
            return nil
        }
        guard
            let fromIndex = viewControllers.firstIndex(of: fromVC),
            let toIndex = viewControllers.firstIndex(of: toVC)
        else {
            return nil
        }
        let direction: TabSlideAnimator.Direction = toIndex > fromIndex ? .left : .right
        return TabSlideAnimator(direction: direction)
    }
}

final class TabSlideAnimator: NSObject, UIViewControllerAnimatedTransitioning {
    enum Direction {
        case left
        case right
    }

    private let direction: Direction

    init(direction: Direction) {
        self.direction = direction
        super.init()
    }

    func transitionDuration(using transitionContext: UIViewControllerContextTransitioning?) -> TimeInterval {
        return 0.30
    }

    func animateTransition(using transitionContext: UIViewControllerContextTransitioning) {
        guard
            let fromView = transitionContext.view(forKey: .from),
            let toView = transitionContext.view(forKey: .to),
            let toVC = transitionContext.viewController(forKey: .to)
        else {
            transitionContext.completeTransition(false)
            return
        }

        let container = transitionContext.containerView
        let finalFrame = transitionContext.finalFrame(for: toVC)
        let width = container.bounds.width
        let offset = direction == .left ? width : -width
        toView.frame = finalFrame.offsetBy(dx: offset, dy: 0)
        container.addSubview(toView)

        UIView.animate(
            withDuration: transitionDuration(using: transitionContext),
            delay: 0,
            options: [.curveEaseInOut]
        ) {
            fromView.frame = fromView.frame.offsetBy(dx: -offset * 0.35, dy: 0)
            fromView.alpha = 0.85
            toView.frame = finalFrame
            toView.alpha = 1.0
            toView.transform = .identity
        } completion: { finished in
            fromView.alpha = 1.0
            fromView.transform = .identity
            transitionContext.completeTransition(finished && !transitionContext.transitionWasCancelled)
        }
    }
}
